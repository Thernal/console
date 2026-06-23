package io.thernal.console.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.ui.NavDisplay
import io.thernal.console.sample.navigation.SampleRoute
import io.thernal.console.sample.screens.brew.BrewScreen
import io.thernal.console.sample.screens.counter.CounterScreen
import io.thernal.console.sample.screens.home.HomeScreen
import io.thernal.console.sample.screens.playground.PlaygroundScreen
import io.thernal.console.sample.theme.SampleTheme
import io.thernal.console.ui.ConsoleProvider

@Composable
fun SampleApp() {
    ConsoleProvider {
        SampleTheme {
            val backStack = remember { mutableStateListOf<NavKey>(SampleRoute.Home) }
            val navigate: (SampleRoute) -> Unit = { backStack.add(it) }
            val back: () -> Unit = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) }

            NavDisplay(
                backStack = backStack,
                onBack = { back() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                entryProvider = entryProvider {
                    entry<SampleRoute.Home> { HomeScreen(onNavigate = navigate) }
                    entry<SampleRoute.Counter> { CounterScreen(onBack = back) }
                    entry<SampleRoute.Brew> { BrewScreen(onBack = back) }
                    entry<SampleRoute.Playground> { PlaygroundScreen(onBack = back) }
                },
            )
        }
    }
}
