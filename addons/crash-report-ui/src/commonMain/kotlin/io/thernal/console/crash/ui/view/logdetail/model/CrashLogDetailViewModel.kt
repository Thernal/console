package io.thernal.console.crash.ui.view.logdetail.model

import androidx.lifecycle.ViewModel
import io.thernal.console.crash.ui.CrashReports
import io.thernal.console.ui.core.IntentHandler
import io.thernal.console.ui.core.StateHolder
import io.thernal.console.ui.logdetail.model.LogDetailIntent
import io.thernal.console.ui.logdetail.model.LogDetailState
import io.thernal.console.ui.logdetail.model.resolveLogGroup

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
        val group = resolveLogGroup(
            sourceLogs = CrashReports.open(sessionId)?.logs.orEmpty(),
            targetLogId = logId,
        )

        snapshot {
            state.logs.set(group.logs)
            state.selectedPageIndex.set(group.initialPageIndex)
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
