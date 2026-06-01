package az.theternal.console.stepper.compose.view.overlay.model

import androidx.compose.runtime.Stable
import az.theternal.console.compose.core.ViewState
import az.theternal.console.stepper.Stepper
import az.theternal.console.stepper.compose.view.overlay.resolveCurrentStepDisplay
import az.theternal.console.stepper.compose.view.overlay.resolveDisplayTag
import az.theternal.console.stepper.compose.view.overlay.resolveStatusText
import az.theternal.console.stepper.compose.view.overlay.resolveStatusTone

@Stable
class StepperOverlayState : ViewState() {
    val isEnabled = field(Stepper.config.value.enabled)
    val isPaused = field(Stepper.config.value.paused)
    val currentLog = field(Stepper.state.value.steppedEvents.lastOrNull())
    val displayTag = field(resolveDisplayTag(Stepper.state.value, Stepper.config.value))
    val canStep = field(Stepper.state.value.blockedLogId != null)
    val caughtCount = field(Stepper.state.value.steppedEvents.size)
    val statusText = field(
        resolveStatusText(
            state = Stepper.state.value,
            canStep = Stepper.state.value.blockedLogId != null,
            isEnabled = Stepper.config.value.enabled,
            isPaused = Stepper.config.value.paused,
        ),
    )
    val statusTone = field(
        resolveStatusTone(
            canStep = Stepper.state.value.blockedLogId != null,
            isEnabled = Stepper.config.value.enabled,
            isPaused = Stepper.config.value.paused,
        ),
    )
    val currentStepDisplay = field(resolveCurrentStepDisplay(Stepper.state.value))
    val isExpanded = field(false)
}
