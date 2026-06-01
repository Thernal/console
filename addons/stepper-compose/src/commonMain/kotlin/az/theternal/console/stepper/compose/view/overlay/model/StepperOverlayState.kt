package az.theternal.console.stepper.compose.view.overlay.model

import androidx.compose.runtime.Stable
import az.theternal.console.compose.core.ViewState
import az.theternal.console.runtime.Log
import az.theternal.console.stepper.compose.view.overlay.StepperStatusTone

@Stable
class StepperOverlayState : ViewState() {
    val isEnabled = field(false)
    val isPaused = field(false)
    val currentLog = field<Log?>(null)
    val displayTag = field<String?>(null)
    val canStep = field(false)
    val caughtCount = field(0)
    val statusText = field("")
    val statusTone = field(StepperStatusTone.Disabled)
    val currentStepDisplay = field<String?>(null)
    val isExpanded = field(false)
}
