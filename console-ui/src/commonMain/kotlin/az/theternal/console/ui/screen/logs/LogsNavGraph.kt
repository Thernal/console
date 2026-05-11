package az.theternal.console.ui.screen.logs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import az.theternal.console.ui.ConsoleRoute
import az.theternal.console.ui.LocalConsoleNavigator
import az.theternal.console.ui.NavGraph
import az.theternal.console.ui.screen.detail.LogDetailScreen

internal object LogsNavGraph : NavGraph {
    override val title = "Logs"
    override val icon: ImageVector = Icons.Default.Menu
    override val order = 0

    @Composable
    override fun Content() {
        val nav = LocalConsoleNavigator.current
        LogsScreen(
            onNavigateToLogDetail = { groupId, logId ->
                nav?.push(ConsoleRoute.LogDetail(groupId, logId))
            },
        )
    }

    override fun EntryProviderScope<NavKey>.routes() {
        entry<ConsoleRoute.LogDetail> { route ->
            val nav = LocalConsoleNavigator.current
            LogDetailScreen(
                logId = route.logId,
                onBack = { nav?.pop() },
            )
        }
    }
}
