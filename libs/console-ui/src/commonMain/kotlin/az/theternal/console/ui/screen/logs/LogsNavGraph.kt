package az.theternal.console.ui.screen.logs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import az.theternal.console.ui.base.NavGraph
import az.theternal.console.ui.nav.ConsoleRoute
import az.theternal.console.ui.nav.LocalConsoleNavController
import az.theternal.console.ui.screen.detail.LogDetailScreen

internal object LogsNavGraph : NavGraph {
    override val title = "Logs"
    override val icon: ImageVector = Icons.Default.Menu

    @Composable
    override fun Content() {
        val nav = LocalConsoleNavController.current
        LogsScreen(
            onNavigateToLogDetail = { groupId, logId ->
                nav.navigate(ConsoleRoute.LogDetail(groupId, logId))
            },
        )
    }

    override fun EntryProviderScope<NavKey>.routes() {
        entry<ConsoleRoute.LogDetail> { route ->
            val nav = LocalConsoleNavController.current
            LogDetailScreen(
                groupId = route.groupId,
                logId = route.logId,
                onBack = { nav.popBack() },
            )
        }
    }
}
