package az.theternal.console.stepper.compose.view.settings.model

import androidx.compose.ui.text.input.TextFieldValue
import az.theternal.console.compose.core.ViewState
import az.theternal.console.stepper.DebugStepper

class DebugStepperSettingsState : ViewState() {
    val config = field(DebugStepper.config.value)
    val stepperState = field(DebugStepper.state.value)
    val tagInput = field(TextFieldValue())
}
