package az.theternal.console.debugstepper.ui.overlay

import az.theternal.console.debugstepper.api.DebugStepper
import az.theternal.console.runtime.model.Log

private const val LOG_ID_PREFIX_LENGTH = 8

internal enum class StepperStatusTone {
    Disabled,
    Paused,
    Idle,
    Running,
}

internal data class DebugStepperOverlayUiState(
    val isEnabled: Boolean,
    val isPaused: Boolean,
    val currentLog: Log?,
    val displayTag: String?,
    val canStep: Boolean,
    val caughtCount: Int,
    val statusText: String,
    val statusTone: StepperStatusTone,
    val currentStepDisplay: String?,
)

internal fun buildDebugStepperOverlayUiState(
    state: DebugStepper.State,
    config: DebugStepper.Config,
): DebugStepperOverlayUiState {
    val currentLog = state.steppedEvents.lastOrNull()
    val displayTag = if (!config.enabled) null else state.blockedTag ?: currentLog?.tag
    val canStep = state.blockedLogId != null
    val currentStepDisplay = state.blockedLogId?.let { id ->
        buildString {
            append("Log #")
            append(id.take(LOG_ID_PREFIX_LENGTH))
            state.blockedTag?.let { append(" [$it]") }
        }
    }

    return DebugStepperOverlayUiState(
        isEnabled = config.enabled,
        isPaused = config.paused,
        currentLog = currentLog,
        displayTag = displayTag,
        canStep = canStep,
        caughtCount = state.steppedEvents.size,
        statusText = resolveStatusText(state, canStep, config.enabled, config.paused),
        statusTone = resolveStatusTone(canStep, config.enabled, config.paused),
        currentStepDisplay = currentStepDisplay,
    )
}

private fun resolveStatusText(
    state: DebugStepper.State,
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

private fun resolveStatusTone(
    canStep: Boolean,
    isEnabled: Boolean,
    isPaused: Boolean,
): StepperStatusTone = when {
    !isEnabled -> StepperStatusTone.Disabled
    isPaused && canStep -> StepperStatusTone.Paused
    isPaused -> StepperStatusTone.Idle
    else -> StepperStatusTone.Running
}
