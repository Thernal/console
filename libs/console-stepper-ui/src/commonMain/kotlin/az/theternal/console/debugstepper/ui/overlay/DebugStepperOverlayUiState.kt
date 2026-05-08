package az.theternal.console.debugstepper.ui.overlay

import androidx.compose.ui.graphics.Color
import az.theternal.console.core.base.Log
import az.theternal.console.debugstepper.DebugStepperState

internal data class DebugStepperOverlayUiState(
    val currentLog: Log?,
    val displayTag: String?,
    val canStep: Boolean,
    val statusText: String,
    val statusColor: Color,
)

internal fun buildDebugStepperOverlayUiState(stepperState: DebugStepperState): DebugStepperOverlayUiState {
    val currentLog = stepperState.events.lastOrNull()
    val displayTag = if (!stepperState.enabled) {
        null
    } else {
        stepperState.blockedTag ?: currentLog?.tag
    }
    val canStep = stepperState.currentStep != null

    val statusText = when {
        !stepperState.enabled -> "Stepper disabled"
        stepperState.paused && canStep -> "Blocked on current log"
        stepperState.paused && stepperState.pendingLogs == 0 -> "No queued logs"
        stepperState.paused -> "Running to next log (${stepperState.pendingLogs} queued)"
        else -> "Running"
    }
    val statusColor = when {
        !stepperState.enabled -> DisabledColor
        stepperState.paused && canStep -> PausedColor
        stepperState.paused -> EmptyColor
        else -> SuccessColor
    }

    return DebugStepperOverlayUiState(
        currentLog = currentLog,
        displayTag = displayTag,
        canStep = canStep,
        statusText = statusText,
        statusColor = statusColor,
    )
}
