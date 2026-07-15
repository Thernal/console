package io.thernal.console.settings.ui.view.settings.model

import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.ui.core.ViewIntent

sealed interface SettingsIntent : ViewIntent {
    data class SetQuery(val query: TextFieldValue) : SettingsIntent
}
