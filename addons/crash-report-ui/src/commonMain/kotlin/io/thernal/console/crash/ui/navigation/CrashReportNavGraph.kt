package io.thernal.console.crash.ui.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.thernal.console.api.addon.ConsoleNavGraph
import io.thernal.console.crash.ui.view.detail.CrashSessionDetailView
import io.thernal.console.crash.ui.view.logdetail.CrashLogDetailView
import io.thernal.console.crash.ui.view.settings.CrashReportSettingsView

data class CrashSessionRoute(val sessionId: String) : NavKey

data class CrashSessionLogRoute(
    val sessionId: String,
    val logId: String,
) : NavKey

data object CrashReportSettingsRoute : NavKey

internal object CrashReportNavGraph : ConsoleNavGraph {
    override fun EntryProviderScope<NavKey>.routes() {
        entry<CrashSessionRoute> { route ->
            CrashSessionDetailView(sessionId = route.sessionId)
        }
        entry<CrashSessionLogRoute> { route ->
            CrashLogDetailView(
                sessionId = route.sessionId,
                logId = route.logId,
            )
        }
        entry<CrashReportSettingsRoute> {
            CrashReportSettingsView()
        }
    }
}
