package io.thernal.console.stepper.ui.agent

import io.thernal.console.agent.AgentConnection
import io.thernal.console.agent.ConsoleAgent
import io.thernal.console.stepper.ui.stepper.Stepper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.concurrent.Volatile

/**
 * Keeps an agent from leaving the app held: a pause made by `stepper_pause` ends when the link to the hub drops (the
 * agent's session ended, or the hub stopped). A pause made in the app is left alone, and so is one the user has
 * already ended.
 */
internal object StepperAgentGuard {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Volatile
    var isPausedByAgent: Boolean = false
        private set

    @Volatile
    private var isStarted = false

    fun start() {
        if (isStarted) return
        isStarted = true
        scope.launch {
            ConsoleAgent.connection.collect { connection ->
                if (connection !is AgentConnection.Connected && isPausedByAgent) {
                    isPausedByAgent = false
                    Stepper.updateConfig { copy(paused = false) }
                }
            }
        }
        scope.launch {
            // Reads the current value rather than the emitted one, so a stale "not paused" never clears a fresh mark.
            Stepper.config.collect { if (!Stepper.config.value.paused) isPausedByAgent = false }
        }
    }

    /** Called right after an agent paused the stepper. */
    fun markPausedByAgent() {
        isPausedByAgent = true
    }
}
