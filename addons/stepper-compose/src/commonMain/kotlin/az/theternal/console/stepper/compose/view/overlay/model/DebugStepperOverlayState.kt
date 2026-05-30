package az.theternal.console.stepper.compose.view.overlay.model

import az.theternal.console.compose.core.ViewState
import az.theternal.console.stepper.DebugStepper
import az.theternal.console.stepper.compose.view.overlay.resolveCurrentStepDisplay
import az.theternal.console.stepper.compose.view.overlay.resolveDisplayTag
import az.theternal.console.stepper.compose.view.overlay.resolveStatusText
import az.theternal.console.stepper.compose.view.overlay.resolveStatusTone

class DebugStepperOverlayState : ViewState() {
    val isEnabled = field(DebugStepper.config.value.enabled)
    val isPaused = field(DebugStepper.config.value.paused)
    val currentLog = field(DebugStepper.state.value.steppedEvents.lastOrNull())
    val displayTag = field(resolveDisplayTag(DebugStepper.state.value, DebugStepper.config.value))
    val canStep = field(DebugStepper.state.value.blockedLogId != null)
    val caughtCount = field(DebugStepper.state.value.steppedEvents.size)
    val statusText = field(
        resolveStatusText(
            state = DebugStepper.state.value,
            canStep = DebugStepper.state.value.blockedLogId != null,
            isEnabled = DebugStepper.config.value.enabled,
            isPaused = DebugStepper.config.value.paused,
        ),
    )
    val statusTone = field(
        resolveStatusTone(
            canStep = DebugStepper.state.value.blockedLogId != null,
            isEnabled = DebugStepper.config.value.enabled,
            isPaused = DebugStepper.config.value.paused,
        ),
    )
    val currentStepDisplay = field(resolveCurrentStepDisplay(DebugStepper.state.value))
    val isExpanded = field(false)
}
