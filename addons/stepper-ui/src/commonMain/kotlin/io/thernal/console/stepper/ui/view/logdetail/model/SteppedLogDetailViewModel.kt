package io.thernal.console.stepper.ui.view.logdetail.model

import androidx.lifecycle.ViewModel
import io.thernal.console.stepper.ui.stepper.Stepper
import io.thernal.console.ui.core.IntentHandler
import io.thernal.console.ui.core.StateHolder
import io.thernal.console.ui.logdetail.model.LogDetailIntent
import io.thernal.console.ui.logdetail.model.LogDetailState
import io.thernal.console.ui.logdetail.model.resolveLogGroup

/**
 * Feeds the shared log-group detail from Stepper's own caught events rather than the live Logs
 * buffer: a paused log may not have reached the logging observer yet (observers emit
 * sequentially and Stepper suspends the loop), and the Logs buffer can be cleared or capped
 * independently — Stepper's list is the only source guaranteed to hold what Stepper shows.
 */
internal class SteppedLogDetailViewModel(
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
            sourceLogs = Stepper.state.value.steppedEvents,
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
