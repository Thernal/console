package io.thernal.console.logging.ui.view.detail.model

import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.Stable
import io.thernal.console.ui.core.derive
import io.thernal.console.ui.core.ViewState
import io.thernal.console.core.log.Log

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
