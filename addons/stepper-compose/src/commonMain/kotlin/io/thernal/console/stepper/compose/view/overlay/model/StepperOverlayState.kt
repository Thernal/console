package io.thernal.console.stepper.compose.view.overlay.model

import androidx.compose.runtime.Stable
import io.thernal.console.compose.core.ViewState
import io.thernal.console.runtime.Log
import io.thernal.console.stepper.compose.view.overlay.StepperStatusTone

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
