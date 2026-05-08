package az.theternal.console.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import az.theternal.console.ui.ConsoleNavigation
import az.theternal.console.ui.screen.console.ConsoleScreen
import az.theternal.console.ui.screen.logs.LogsNavGraph

@Composable
internal fun ConsoleNavHost(onClose: () -> Unit) {
    remember { ConsoleNavigation.register(LogsNavGraph) }
    val backStack = remember { mutableStateListOf<NavKey>(ConsoleRoute.Main) }
    val navController = remember(backStack, onClose) {
        ConsoleNavController(backStack = backStack, onClose = onClose)
    }
    val graphs = ConsoleNavigation.graphs

    CompositionLocalProvider(LocalConsoleNavController provides navController) {
        NavDisplay(
            backStack = backStack,
            onBack = { navController.popBack() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                entry<ConsoleRoute.Main> {
                    ConsoleScreen(onClose = onClose)
                }
                graphs.forEach { graph ->
                    with(graph) { routes() }
                }
            },
        )
    }
}
