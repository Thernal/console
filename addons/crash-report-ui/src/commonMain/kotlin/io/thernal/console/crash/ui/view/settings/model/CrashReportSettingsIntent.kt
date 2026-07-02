package io.thernal.console.crash.ui.view.settings.model

import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.core.log.LogLevel
import io.thernal.console.crash.ui.config.CrashBodyPolicy
import io.thernal.console.ui.core.ViewIntent

internal sealed interface CrashReportSettingsIntent : ViewIntent {

    data class SetShowSafeSessions(val showSafeSessions: Boolean) : CrashReportSettingsIntent

    data class SetPersistOnMatch(val persistOnMatch: Boolean) : CrashReportSettingsIntent

    data class SetPersistLevelAtLeast(val level: LogLevel?) : CrashReportSettingsIntent

    data class SetBodyPolicy(val policy: CrashBodyPolicy) : CrashReportSettingsIntent

    data class SetIncludeTagInput(val value: TextFieldValue) : CrashReportSettingsIntent

    data object AddIncludeTag : CrashReportSettingsIntent

    data class RemoveIncludeTag(val tag: String) : CrashReportSettingsIntent

    data class SetExcludeTagInput(val value: TextFieldValue) : CrashReportSettingsIntent

    data object AddExcludeTag : CrashReportSettingsIntent

    data class RemoveExcludeTag(val tag: String) : CrashReportSettingsIntent
}
