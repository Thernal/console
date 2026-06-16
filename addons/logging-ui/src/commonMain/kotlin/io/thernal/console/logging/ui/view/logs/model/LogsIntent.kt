package io.thernal.console.logging.ui.view.logs.model

import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.ui.core.ViewIntent
import io.thernal.console.core.log.LogLevel

sealed interface LogsIntent : ViewIntent {
    data class SetQuery(val query: TextFieldValue) : LogsIntent
    data class ToggleTag(val tag: String) : LogsIntent
    data object SelectAllTags : LogsIntent
    data class ToggleLevel(val level: LogLevel) : LogsIntent
    data object SelectAllLevels : LogsIntent
}
