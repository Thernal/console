package az.theternal.console.debugstepper.api

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import az.theternal.console.debugstepper.ui.DebugStepperNavGraph
import az.theternal.console.debugstepper.ui.overlay.DebugStepperOverlay
import az.theternal.console.ConsoleScope
import az.theternal.console.LogObserver
import az.theternal.console.Log
import az.theternal.console.LogLevel
import az.theternal.console.addon.api.ConsoleAddon
import az.theternal.console.addon.api.nav.ConsoleNavGraph
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.concurrent.Volatile

object DebugStepper : LogObserver, ConsoleAddon {

    private const val DEFAULT_MAX_STEPPED_EVENT_COUNT = 50
    private const val SECONDS_TO_MILLIS = 1_000L

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()
    private val _config = MutableStateFlow(Config())
    val config: StateFlow<Config> = _config.asStateFlow()

    @Volatile
    private var currentWaiter: CompletableDeferred<Unit>? = null

    fun updateConfig(config: Config) {
        val normalized = config.copy(maxSteppedEventCount = config.maxSteppedEventCount.coerceAtLeast(0))
        _config.value = normalized
        _state.update { current ->
            val trimmedEvents = when {
                normalized.maxSteppedEventCount <= 0 -> emptyList()
                current.steppedEvents.size <= normalized.maxSteppedEventCount -> current.steppedEvents
                else -> current.steppedEvents.takeLast(normalized.maxSteppedEventCount)
            }
            current.copy(steppedEvents = trimmedEvents)
        }
        if (!normalized.enabled || !normalized.paused) releaseCurrentWaiter()
    }

    fun updateConfig(block: Config.() -> Config) {
        updateConfig(_config.value.block())
    }

    fun next() {
        releaseCurrentWaiter()
    }

    fun clearSteppedEvents() {
        _state.update { it.copy(steppedEvents = emptyList()) }
    }

    // ── LogObserver ───────────────────────────────────────────────────────────

    override suspend fun emit(event: Log) {
        _state.update { it.copy(pendingLogs = it.pendingLogs + 1) }
        try {
            awaitIfNeeded(event)
        } finally {
            _state.update { it.copy(pendingLogs = (it.pendingLogs - 1).coerceAtLeast(0)) }
        }
    }

    // ── ConsoleAddon ──────────────────────────────────────────────────────────

    override fun onInstall(console: ConsoleScope) {
        console.addObserver(this)
    }

    override fun navGraph(): ConsoleNavGraph {
        return DebugStepperNavGraph
    }

    override fun overlay(): @Composable BoxScope.() -> Unit = { DebugStepperOverlay() }

    // ── Private ───────────────────────────────────────────────────────────────

    private suspend fun awaitIfNeeded(event: Log) {
        val config = _config.value
        if (!config.enabled || !config.paused) return
        if (!shouldPause(event, config)) return

        val waiter = CompletableDeferred<Unit>()
        currentWaiter = waiter

        if (!config.enabled || !config.paused || waiter.isCompleted) {
            currentWaiter = null
            return
        }

        _state.update {
            val steppedEvents = if (config.maxSteppedEventCount <= 0) {
                emptyList()
            } else {
                (it.steppedEvents + event).takeLast(config.maxSteppedEventCount)
            }
            it.copy(
                blockedLogId = event.id,
                blockedTag = event.tag,
                steppedEvents = steppedEvents,
            )
        }
        try {
            val timeoutMs = config.autoResumeSeconds?.let { it.toLong() * SECONDS_TO_MILLIS }
            if (timeoutMs != null) {
                withTimeoutOrNull(timeoutMs) { waiter.await() }
            } else {
                waiter.await()
            }
        } finally {
            currentWaiter = null
            _state.update { it.copy(blockedLogId = null, blockedTag = null) }
        }
    }

    private fun shouldPause(
        event: Log,
        config: Config,
    ): Boolean {
        if (!config.pauseOnMatch) return true
        if (config.pauseOnTags.isNotEmpty() && event.tag !in config.pauseOnTags) return false
        val minLevel = config.pauseOnLevelAtLeast ?: return true
        if (event.level == LogLevel.None) return false
        return event.level.ordinal >= minLevel.ordinal
    }

    private fun releaseCurrentWaiter() {
        currentWaiter?.complete(Unit)
    }

    data class Config(
        val enabled: Boolean = false,
        val paused: Boolean = false,
        val pauseOnMatch: Boolean = false,
        val pauseOnTags: Set<String> = emptySet(),
        val pauseOnLevelAtLeast: LogLevel? = null,
        val autoResumeSeconds: Int? = null,
        val maxSteppedEventCount: Int = DEFAULT_MAX_STEPPED_EVENT_COUNT,
    )

    data class State(
        val steppedEvents: List<Log> = emptyList(),
        val pendingLogs: Int = 0,
        val blockedLogId: String? = null,
        val blockedTag: String? = null,
    )
}
