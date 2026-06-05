package io.thernal.console.stepper.compose.view.settings.model

import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.compose.core.ViewIntent

sealed interface StepperSettingsIntent : ViewIntent {
    data class SetTagInput(val value: TextFieldValue) : StepperSettingsIntent
    data object AddTag : StepperSettingsIntent
}
