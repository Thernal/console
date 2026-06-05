package io.thernal.console.compose.view.logs.model

import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.compose.core.ViewIntent
import io.thernal.console.runtime.log.LogLevel

sealed interface LogsIntent : ViewIntent {
    data class SetQuery(val query: TextFieldValue) : LogsIntent
    data class ToggleTag(val tag: String) : LogsIntent
    data object SelectAllTags : LogsIntent
    data class ToggleLevel(val level: LogLevel) : LogsIntent
    data object SelectAllLevels : LogsIntent
}
