package az.theternal.console.stepper.compose.view.settings.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue
import az.theternal.console.compose.core.ViewState
import az.theternal.console.runtime.Log
import az.theternal.console.runtime.LogLevel

@Stable
class StepperSettingsState : ViewState() {
    val enabled = field(false)
    val paused = field(false)
    val pauseOnMatch = field(false)
    val pauseOnTags = field(emptySet<String>())
    val pauseOnLevelAtLeast = field<LogLevel?>(null)
    val autoResumeSeconds = field<Int?>(null)
    val isStepperActive = field(false)
    val steppedEvents = field(emptyList<Log>())
    val tagInput = field(TextFieldValue())
}
