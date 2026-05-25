package az.theternal.console.compose.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import az.theternal.console.api.addon.ConsoleNavGraph
import az.theternal.console.api.navigation.ConsoleRoute
import az.theternal.console.compose.view.detail.LogDetailView

internal object LogsNavGraph : ConsoleNavGraph {
    override fun EntryProviderScope<NavKey>.routes() {
        entry<ConsoleRoute.LogDetail> { route ->
            LogDetailView(
                logId = route.logId,
            )
        }
    }
}
