package io.thernal.console.crash.ui.view.settings.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.core.log.LogLevel
import io.thernal.console.crash.ui.config.CrashBodyPolicy
import io.thernal.console.ui.core.ViewState

@Stable
class CrashReportSettingsState : ViewState() {

    val showSafeSessions = field(false)

    val persistOnMatch = field(false)

    val persistLevelAtLeast = field<LogLevel?>(null)

    val includeTags = field(emptySet<String>())

    val excludeTags = field(emptySet<String>())

    val bodyPolicy = field(CrashBodyPolicy.Full)

    val includeTagInput = field(TextFieldValue())

    val excludeTagInput = field(TextFieldValue())
}
