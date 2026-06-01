package az.theternal.console.stepper.compose.view.settings.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue
import az.theternal.console.compose.core.ViewState
import az.theternal.console.stepper.Stepper

@Stable
class StepperSettingsState : ViewState() {
    val enabled = field(Stepper.config.value.enabled)
    val paused = field(Stepper.config.value.paused)
    val pauseOnMatch = field(Stepper.config.value.pauseOnMatch)
    val pauseOnTags = field(Stepper.config.value.pauseOnTags)
    val pauseOnLevelAtLeast = field(Stepper.config.value.pauseOnLevelAtLeast)
    val autoResumeSeconds = field(Stepper.config.value.autoResumeSeconds)
    val isStepperActive = field(Stepper.config.value.run { enabled && paused })
    val steppedEvents = field(Stepper.state.value.steppedEvents)
    val tagInput = field(TextFieldValue())
}
