package az.theternal.console.compose.view.logs.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue
import az.theternal.console.compose.core.ViewState
import az.theternal.console.runtime.Log
import az.theternal.console.runtime.LogLevel

@Stable
class LogsState : ViewState() {
    val logs = field(emptyList<Log>())
    val searchQuery = field(TextFieldValue())
    val selectedTags = field(emptySet<String>())
    val selectedLevels = field(emptySet<LogLevel>())
    val allLevels = field(LogLevel.entries.filter { it != LogLevel.None })
    val tags = field(emptyList<String>())
    val hasAnyLogs = field(false)
}
