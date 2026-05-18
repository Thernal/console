package az.theternal.console.ui.nav

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import az.theternal.console.addon.api.nav.ConsoleNavigation
import az.theternal.console.addon.api.nav.ConsoleRoute
import az.theternal.console.addon.api.nav.ConsoleTab
import az.theternal.console.ui.screen.console.ConsoleScreen
import az.theternal.console.ui.screen.logs.LogsNavGraph

@Composable
internal fun ConsoleNavHost(
    navController: ConsoleNavigatorImpl,
    requestedTab: ConsoleTab?,
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
        transitionSpec = {
            slideInHorizontally { it / 2 } + fadeIn() togetherWith ExitTransition.None
        },
        popTransitionSpec = {
            EnterTransition.None togetherWith slideOutHorizontally { it / 2 } + fadeOut()
        },
        predictivePopTransitionSpec = {
            EnterTransition.None togetherWith fadeOut() + slideOutHorizontally(
                targetOffsetX = { fullWidth ->
                    if (it != 0) {
                        fullWidth / 3 + it / 2
                    } else {
                        fullWidth / 3
                    }
                },
            )
        },
        entryProvider = entryProvider {
            entry<ConsoleRoute.Stub> { }
            entry<ConsoleRoute.Main> {
                ConsoleScreen(
                    onClose = { navController.close() },
                    requestedTab = requestedTab,
                    onRequestConsumed = onRequestConsumed,
                )
            }
            graphs.forEach { graph ->
                with(graph) { routes() }
            }
        },
    )
}
