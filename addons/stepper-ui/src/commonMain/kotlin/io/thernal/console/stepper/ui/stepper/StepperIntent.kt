package io.thernal.console.stepper.ui.stepper

import io.thernal.console.ui.core.ViewIntent
import io.thernal.console.core.log.LogLevel

sealed interface StepperIntent : ViewIntent {
    data class SetEnabled(val isEnabled: Boolean) : StepperIntent
    data object ToggleEnabled : StepperIntent
    data class SetPaused(val isPaused: Boolean) : StepperIntent
    data object TogglePaused : StepperIntent
    data class SetPauseOnMatch(val shouldPauseOnMatch: Boolean) : StepperIntent
    data class AddPauseTag(val tag: String) : StepperIntent
    data class RemovePauseTag(val tag: String) : StepperIntent
    data class SetPauseOnLevelAtLeast(val level: LogLevel?) : StepperIntent
    data class SetAutoResumeSeconds(val seconds: Int?) : StepperIntent
    data object ClearSteppedEvents : StepperIntent
    data object Next : StepperIntent
}
