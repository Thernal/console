package io.thernal.console.stepper.compose.view.settings.model

import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.compose.core.Intent
import io.thernal.console.runtime.LogLevel

sealed interface StepperIntent : Intent {
    data class SetTagInput(val value: TextFieldValue) : StepperIntent
    data object AddTag : StepperIntent
    data class SetEnabled(val enabled: Boolean) : StepperIntent
    data class SetPaused(val paused: Boolean) : StepperIntent
    data class SetPauseOnMatch(val pauseOnMatch: Boolean) : StepperIntent
    data class RemovePauseTag(val tag: String) : StepperIntent
    data class SetPauseOnLevelAtLeast(val level: LogLevel?) : StepperIntent
    data class SetAutoResumeSeconds(val seconds: Int?) : StepperIntent
    data object ClearSteppedEvents : StepperIntent
}
