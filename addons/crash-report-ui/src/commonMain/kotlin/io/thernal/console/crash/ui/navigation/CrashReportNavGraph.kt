package io.thernal.console.crash.ui.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.thernal.console.api.addon.ConsoleNavGraph
import io.thernal.console.crash.ui.view.detail.CrashSessionDetailView

data class CrashSessionRoute(val sessionId: String) : NavKey

internal object CrashReportNavGraph : ConsoleNavGraph {
    override fun EntryProviderScope<NavKey>.routes() {
        entry<CrashSessionRoute> { route ->
            CrashSessionDetailView(sessionId = route.sessionId)
        }
    }
}
