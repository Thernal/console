package io.thernal.console.compose.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.thernal.console.api.addon.ConsoleNavigation
import io.thernal.console.api.navigation.ConsoleNavigator
import io.thernal.console.api.navigation.ConsoleRoute
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.compose.view.console.ConsoleView

@Composable
internal fun ConsoleNavHost(
    navController: ConsoleNavigator,
    backStack: SnapshotStateList<NavKey>,
    requestedTab: ConsoleTab?,
    onRequestConsumed: () -> Unit,
) {
    NavDisplay(
        backStack = backStack,
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
                ConsoleView(
                    requestedTab = requestedTab,
                    onRequestConsumed = onRequestConsumed,
                )
            }

            ConsoleNavigation.graphs.forEach { graph ->
                with(graph) { routes() }
            }
        },
    )
}
