package az.theternal.console.stepper.compose.view.settings.model

import androidx.compose.ui.text.input.TextFieldValue
import az.theternal.console.compose.core.Intent
import az.theternal.console.runtime.LogLevel

sealed interface DebugStepperIntent : Intent {
    data class SetTagInput(val value: TextFieldValue) : DebugStepperIntent
    data object AddTag : DebugStepperIntent
    data class SetEnabled(val enabled: Boolean) : DebugStepperIntent
    data class SetPaused(val paused: Boolean) : DebugStepperIntent
    data class SetPauseOnMatch(val pauseOnMatch: Boolean) : DebugStepperIntent
    data class RemovePauseTag(val tag: String) : DebugStepperIntent
    data class SetPauseOnLevelAtLeast(val level: LogLevel?) : DebugStepperIntent
    data class SetAutoResumeSeconds(val seconds: Int?) : DebugStepperIntent
    data object ClearSteppedEvents : DebugStepperIntent
}
