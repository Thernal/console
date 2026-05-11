package az.theternal.console.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import az.theternal.console.ui.ConsoleNavigation
import az.theternal.console.ui.ConsoleRoute
import az.theternal.console.ui.screen.console.ConsoleScreen
import az.theternal.console.ui.screen.logs.LogsNavGraph

@Composable
internal fun ConsoleNavHost(
    navController: ConsoleNavigatorImpl,
    requestedTabTitle: String?,
    onRequestConsumed: () -> Unit,
) {
    remember { ConsoleNavigation.register(LogsNavGraph) }
    val graphs = ConsoleNavigation.graphs

    NavDisplay(
        backStack = navController.backStack,
        onBack = { navController.pop() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<ConsoleRoute.Stub> { }
            entry<ConsoleRoute.Main> {
                ConsoleScreen(
                    onClose = { navController.close() },
                    requestedTabTitle = requestedTabTitle,
                    onRequestConsumed = onRequestConsumed,
                )
            }
            graphs.forEach { graph ->
                with(graph) { routes() }
            }
        },
    )
}
