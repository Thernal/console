package io.thernal.console.settings.ui.view.settings.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.settings.SettingsSection
import io.thernal.console.ui.core.ViewState

@Stable
class SettingsState : ViewState() {
    val sections = field(emptyList<SettingsSection>())
    val searchQuery = field(TextFieldValue())
}
