package io.thernal.console.crash.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.crash.ui.view.sessions.CrashSessionsView
import io.thernal.console.crash.ui.view.sessions.components.CrashSessionsActions

internal object CrashReportTab : ConsoleTab {
    override val title: String = "Crashes"
    override val icon: ImageVector = Icons.Outlined.ReportProblem

    @Composable
    override fun Content() {
        CrashSessionsView()
    }

    @Composable
    override fun Actions() {
        CrashSessionsActions()
    }
}
