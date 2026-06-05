package io.thernal.console.compose.view.detail.model

import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.Stable
import io.thernal.console.compose.core.derive
import io.thernal.console.compose.core.ViewState
import io.thernal.console.runtime.log.Log

@Stable
class LogDetailState : ViewState() {
    val isInitialized = field(false)
    val searchQuery = field(TextFieldValue())
    val logs = field<List<Log>>(emptyList())
    val selectedPageIndex = field(0)

    val activeLog = logs.derive { currentLogs ->
        currentLogs.getOrNull(selectedPageIndex.value)
    }

    val showTabs = logs.derive { currentLogs ->
        currentLogs.size > 1
    }

    val showSearch = logs.derive { currentLogs ->
        currentLogs.isNotEmpty()
    }
}
