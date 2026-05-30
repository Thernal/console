package az.theternal.console.compose.view.logs.model

import androidx.compose.ui.text.input.TextFieldValue
import az.theternal.console.compose.core.Intent
import az.theternal.console.runtime.LogLevel

sealed interface LogsIntent : Intent {
    data class SetQuery(val query: TextFieldValue) : LogsIntent
    data class ToggleTag(val tag: String) : LogsIntent
    data object SelectAllTags : LogsIntent
    data class ToggleLevel(val level: LogLevel) : LogsIntent
    data object SelectAllLevels : LogsIntent
}
