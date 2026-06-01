package az.theternal.console.stepper.compose.view.overlay

import az.theternal.console.stepper.Stepper

private const val LOG_ID_PREFIX_LENGTH = 8

enum class StepperStatusTone {
    Disabled,
    Paused,
    Idle,
    Running,
}

internal fun resolveDisplayTag(
    state: Stepper.State,
    config: Stepper.Config,
): String? {
    val currentLog = state.steppedEvents.lastOrNull()
    return if (!config.enabled) null else state.blockedTag ?: currentLog?.tag
}

internal fun resolveCurrentStepDisplay(state: Stepper.State): String? {
    return state.blockedLogId?.let { id ->
        buildString {
            append("Log #")
            append(id.take(LOG_ID_PREFIX_LENGTH))
            state.blockedTag?.let { append(" [$it]") }
        }
    }
}

fun resolveStatusText(
    state: Stepper.State,
    canStep: Boolean,
    isEnabled: Boolean,
    isPaused: Boolean,
): String = when {
    !isEnabled -> "Disabled"
    isPaused && canStep -> "Paused"
    isPaused && state.pendingLogs == 0 -> "Idle"
    isPaused -> "Running · ${state.pendingLogs} queued"
    else -> "Running"
}

fun resolveStatusTone(
    canStep: Boolean,
    isEnabled: Boolean,
    isPaused: Boolean,
): StepperStatusTone = when {
    !isEnabled -> StepperStatusTone.Disabled
    isPaused && canStep -> StepperStatusTone.Paused
    isPaused -> StepperStatusTone.Idle
    else -> StepperStatusTone.Running
}
