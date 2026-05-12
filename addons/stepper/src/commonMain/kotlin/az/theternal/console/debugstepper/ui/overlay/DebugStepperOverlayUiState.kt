package az.theternal.console.debugstepper.ui.overlay

import az.theternal.console.core.base.Log
import az.theternal.console.debugstepper.DebugStepperState

private const val LOG_ID_PREFIX_LENGTH = 8

internal enum class StepperStatusTone {
    Disabled,
    Paused,
    Idle,
    Running,
}

internal data class DebugStepperOverlayUiState(
    val currentLog: Log?,
    val displayTag: String?,
    val canStep: Boolean,
    val statusText: String,
    val statusTone: StepperStatusTone,
    val currentStepDisplay: String?,
)

internal fun buildDebugStepperOverlayUiState(stepperState: DebugStepperState): DebugStepperOverlayUiState {
    val currentLog = stepperState.steppedEvents.lastOrNull()
    val displayTag = if (!stepperState.enabled) null else stepperState.blockedTag ?: currentLog?.tag
    val canStep = stepperState.blockedLogId != null
    val currentStepDisplay = stepperState.blockedLogId?.let { id ->
        buildString {
            append("Log #")
            append(id.take(LOG_ID_PREFIX_LENGTH))
            stepperState.blockedTag?.let { append(" [$it]") }
        }
    }

    return DebugStepperOverlayUiState(
        currentLog = currentLog,
        displayTag = displayTag,
        canStep = canStep,
        statusText = resolveStatusText(stepperState, canStep),
        statusTone = resolveStatusTone(stepperState, canStep),
        currentStepDisplay = currentStepDisplay,
    )
}

private fun resolveStatusText(
    state: DebugStepperState,
    canStep: Boolean,
): String = when {
    !state.enabled -> "Disabled"
    state.paused && canStep -> "Paused"
    state.paused && state.pendingLogs == 0 -> "Idle"
    state.paused -> "Running · ${state.pendingLogs} queued"
    else -> "Running"
}

private fun resolveStatusTone(
    state: DebugStepperState,
    canStep: Boolean,
): StepperStatusTone = when {
    !state.enabled -> StepperStatusTone.Disabled
    state.paused && canStep -> StepperStatusTone.Paused
    state.paused -> StepperStatusTone.Idle
    else -> StepperStatusTone.Running
}
