package io.thernal.console.crash.ui.view.logdetail.model

import androidx.lifecycle.ViewModel
import io.thernal.console.core.log.Log
import io.thernal.console.crash.ui.CrashReports
import io.thernal.console.ui.core.IntentHandler
import io.thernal.console.ui.core.StateHolder
import io.thernal.console.ui.logdetail.model.LogDetailIntent
import io.thernal.console.ui.logdetail.model.LogDetailState

/**
 * Feeds the shared log-group detail from a persisted crash session rather than the live pipeline:
 * restored logs are never re-emitted through `Console.notify`, so `logging-ui`'s detail (which
 * reads its live observer) cannot find them. Grouping works the same way — `groupId` survives the
 * round-trip, so a restored request/response pair pages together.
 */
internal class CrashLogDetailViewModel(
    sessionId: String,
    logId: String,
) : ViewModel(),
    StateHolder,
    IntentHandler<LogDetailIntent> {

    val state = LogDetailState()

    override val handler = onIntentUpdate { intent ->
        when (intent) {
            is LogDetailIntent.SelectPage -> selectPage(pageIndex = intent.pageIndex)
            is LogDetailIntent.SetQuery -> state.searchQuery.set(intent.query)
        }
    }

    init {
        val sessionLogs = CrashReports.open(sessionId)?.logs.orEmpty()
        val target = sessionLogs.firstOrNull { it.id == logId }
        val groupLogs = when {
            target == null -> emptyList()

            target.groupId == null -> listOf(target)

            else ->
                sessionLogs
                    .filter { it.id == logId || it.groupId == target.groupId }
                    .sortedBy(Log::timestamp)
        }
        val initialPageIndex = groupLogs.indexOfFirst { it.id == logId }
            .takeIf { it >= 0 } ?: 0

        snapshot {
            state.logs.set(groupLogs)
            state.selectedPageIndex.set(initialPageIndex)
            state.isInitialized.set(true)
        }
    }

    private fun selectPage(pageIndex: Int) {
        val tabs = state.logs.value
        if (tabs.isEmpty()) {
            return
        }
        state.selectedPageIndex.set(pageIndex.coerceIn(0, tabs.lastIndex))
    }
}
