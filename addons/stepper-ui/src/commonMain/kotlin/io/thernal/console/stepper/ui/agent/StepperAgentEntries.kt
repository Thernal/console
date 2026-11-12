package io.thernal.console.stepper.ui.agent

import io.thernal.console.agent.AgentEntries
import io.thernal.console.agent.AgentEntriesScope
import io.thernal.console.agent.agentEntries
import io.thernal.console.core.log.LogLevel
import io.thernal.console.stepper.ui.stepper.AutoResumeDelay
import io.thernal.console.stepper.ui.stepper.Stepper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

private const val DEFAULT_NEXT_WAIT_SECONDS = 3
private const val MAX_WAIT_SECONDS = 10
private const val MILLIS_PER_SECOND = 1_000L
private const val WAIT_AGAIN = "`stepper_state` with `wait_seconds` waits again."

/**
 * What the stepper shares with an AI agent: its state, a feed of each log it holds and lets go, and pausing, stepping
 * and resuming. None of the actions logs through Console, since a log written while the pipeline is held would wait
 * for it.
 */
internal fun stepperAgentEntries(): AgentEntries {
    return agentEntries(
        id = "stepper",
        title = "Stepper",
        about = "Holds the log pipeline, and the code waiting on it, at matching logs so a flow runs step by step",
    ) {
        tool(
            name = "stepper_state",
            description = "On or off, paused, the rules, and the log held now; can wait for a log to be held",
        ) {
            val wait = int(
                key = "wait_seconds",
                description = "When nothing is held, wait this long for a log to be held; 0 answers at once",
                default = 0,
                min = 0,
                max = MAX_WAIT_SECONDS,
            )
            run { call ->
                if (call[wait] > 0) {
                    withTimeoutOrNull(call[wait] * MILLIS_PER_SECOND) {
                        Stepper.state.first { it.blockedLogId != null }
                    }
                }
                currentStepperState()
            }
        }
        feed(
            name = "stepper_events",
            description = "Each log the stepper held (kind Held) and let go (kind Released)",
            records = stepperEvents(Stepper.state),
        )
        pauseAction()
        nextAction()
        action(name = "stepper_resume", description = "Stops holding and lets every held log go; keeps the rules") {
            run { resume() }
        }
    }
}

private fun AgentEntriesScope.pauseAction() {
    action(
        name = "stepper_pause",
        description = "Turns the stepper on and holds the pipeline at matching logs: every log when neither tags " +
            "nor level is given. Ends by itself when the hub disconnects",
    ) {
        val tags = optionalText(key = "tags", description = "Comma-separated log tags to hold at")
        val level = optionalChoice(
            key = "level",
            description = "Hold only logs at this level or above",
            options = LogLevel.entries.filter { it != LogLevel.None }.map { it.name },
        )
        val autoResume = optionalChoice(
            key = "auto_resume_seconds",
            description = "Let each held log go after this many seconds; omit to hold until `stepper_next`",
            options = AutoResumeDelay.entries.map { it.seconds.toString() },
        )
        run { call ->
            pause(
                tags = call[tags].orEmpty().split(',').map(String::trim).filter(String::isNotEmpty).toSet(),
                level = call[level]?.let { name -> LogLevel.entries.first { it.name.equals(name, ignoreCase = true) } },
                autoResumeSeconds = call[autoResume]?.toInt(),
            )
        }
    }
}

private fun AgentEntriesScope.nextAction() {
    action(
        name = "stepper_next",
        description = "Lets the held log go, then waits a moment for the next one and names it",
    ) {
        val wait = int(
            key = "wait_seconds",
            description = "How long to wait for the next held log; 0 answers at once",
            default = DEFAULT_NEXT_WAIT_SECONDS,
            min = 0,
            max = MAX_WAIT_SECONDS,
        )
        run { call -> next(waitSeconds = call[wait]) }
    }
}

private fun currentStepperState(): String {
    return stepperStateText(Stepper.config.value, Stepper.state.value, StepperAgentGuard.isPausedByAgent)
}

private fun pause(
    tags: Set<String>,
    level: LogLevel?,
    autoResumeSeconds: Int?,
): String {
    Stepper.updateConfig {
        copy(
            enabled = true,
            paused = true,
            pauseOnMatch = tags.isNotEmpty() || level != null,
            pauseOnTags = tags,
            pauseOnLevelAtLeast = level,
            autoResumeSeconds = autoResumeSeconds,
        )
    }
    StepperAgentGuard.markPausedByAgent()
    return currentStepperState()
}

private suspend fun next(waitSeconds: Int): String {
    val state = Stepper.state.value
    val releasedId = state.blockedLogId
    if (releasedId == null) {
        val hint = if (Stepper.config.value.paused) {
            "the flow has not reached a log the stepper holds; `stepper_state` with `wait_seconds` waits for one."
        } else {
            "the stepper is not paused; `stepper_pause` starts holding."
        }
        return "Nothing is held now: $hint"
    }
    val released = heldText(state).removePrefix("Held now: ")
    Stepper.next()
    if (waitSeconds == 0) return "Let go: $released"
    val nextState = withTimeoutOrNull(waitSeconds * MILLIS_PER_SECOND) {
        Stepper.state.first { it.blockedLogId != null && it.blockedLogId != releasedId }
    }
    val after = if (nextState != null) heldText(nextState) else "Nothing held within $waitSeconds s; $WAIT_AGAIN"
    return "Let go: $released\n$after"
}

private fun resume(): String {
    val config = Stepper.config.value
    if (!config.paused) return "The stepper was not paused; nothing held."
    val held = Stepper.state.value.pendingLogs
    Stepper.updateConfig { copy(paused = false) }
    return "Resumed: let $held held or queued logs go. The stepper stays on with its rules; " +
        "`stepper_pause` holds again."
}
