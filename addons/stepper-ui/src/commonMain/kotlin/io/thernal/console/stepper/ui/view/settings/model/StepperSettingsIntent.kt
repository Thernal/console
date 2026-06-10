package io.thernal.console.stepper.ui.view.settings.model

import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.ui.core.ViewIntent

sealed interface StepperSettingsIntent : ViewIntent {
    data class SetTagInput(val value: TextFieldValue) : StepperSettingsIntent
    data object AddTag : StepperSettingsIntent
}
