package io.thernal.console.logging.ui.view.logs.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.ui.core.ViewState
import io.thernal.console.runtime.log.Log
import io.thernal.console.runtime.log.LogLevel

@Stable
class LogsState : ViewState() {
    val logs = field(emptyList<Log>())
    val searchQuery = field(TextFieldValue())
    val selectedTags = field(emptySet<String>())
    val selectedLevels = field(emptySet<LogLevel>())
    val tags = field(emptyList<String>())
    val hasAnyLogs = field(false)
}
