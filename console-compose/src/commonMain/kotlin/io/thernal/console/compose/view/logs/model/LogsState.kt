package io.thernal.console.compose.view.logs.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.compose.core.ViewState
import io.thernal.console.runtime.Log
import io.thernal.console.runtime.LogLevel

@Stable
class LogsState : ViewState() {
    val logs = field(emptyList<Log>())
    val searchQuery = field(TextFieldValue())
    val selectedTags = field(emptySet<String>())
    val selectedLevels = field(emptySet<LogLevel>())
    val tags = field(emptyList<String>())
    val hasAnyLogs = field(false)
}
