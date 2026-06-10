package io.thernal.console.logging.ui.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.thernal.console.api.addon.ConsoleNavGraph
import io.thernal.console.api.navigation.ConsoleRoute
import io.thernal.console.logging.ui.view.detail.LogDetailView

internal object LogsNavGraph : ConsoleNavGraph {
    override fun EntryProviderScope<NavKey>.routes() {
        entry<ConsoleRoute.LogDetail> { route ->
            LogDetailView(
                logId = route.logId,
                groupId = route.groupId,
            )
        }
    }
}
