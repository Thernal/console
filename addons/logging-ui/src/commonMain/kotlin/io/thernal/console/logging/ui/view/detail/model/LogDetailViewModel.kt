package io.thernal.console.logging.ui.view.detail.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.logging.ui.ConsoleLogObserver
import io.thernal.console.ui.core.IntentHandler
import io.thernal.console.ui.core.StateHolder
import io.thernal.console.ui.logdetail.model.LogDetailIntent
import io.thernal.console.ui.logdetail.model.LogDetailState
import io.thernal.console.core.log.Log
import kotlinx.coroutines.launch

class LogDetailViewModel(
    private val logId: String,
    private val groupId: String,
) : ViewModel(), StateHolder, IntentHandler<LogDetailIntent> {
    val state = LogDetailState()

    override val handler = onIntentUpdate { intent ->
        when (intent) {
            is LogDetailIntent.SelectPage -> selectPage(pageIndex = intent.pageIndex)

            is LogDetailIntent.SetQuery -> {
                state.searchQuery.set(intent.query)
            }
        }
    }

    init {
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {
            val logs = ConsoleLogObserver.logs.value
                .filter { log ->
                    log.id == logId || log.groupId == groupId
                }
                .sortedBy(Log::timestamp)

            val initialPageIndex = logs.indexOfFirst { it.id == logId }
                .takeIf { it >= 0 } ?: 0

            snapshot {
                state.logs.set(logs)
                state.selectedPageIndex.set(initialPageIndex)
                state.isInitialized.set(true)
            }
        }
    }

    private fun selectPage(pageIndex: Int) {
        val tabs = state.logs.value
        if (tabs.isEmpty()) {
            return
        }

        val resolvedPageIndex = pageIndex.coerceIn(0, tabs.lastIndex)
        state.selectedPageIndex.set(resolvedPageIndex)
    }
}
