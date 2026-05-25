package az.theternal.console.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import az.theternal.console.api.addon.ConsoleTab
import az.theternal.console.api.navigation.ConsoleNavigator
import az.theternal.console.api.navigation.ConsoleRoute
import az.theternal.console.api.navigation.LocalConsoleNavigator
import az.theternal.console.api.addon.ConsoleOverlays
import az.theternal.console.api.ui.LocalLogRenderer
import az.theternal.console.api.ui.LogRenderer
import az.theternal.console.runtime.Console
import az.theternal.console.compose.renderer.DefaultLogRenderer
import az.theternal.console.compose.trigger.ConsoleTrigger
import az.theternal.console.compose.trigger.Swipe
import az.theternal.console.compose.navigation.ConsoleNavHost
import az.theternal.console.compose.navigation.ConsoleNavigatorImpl
import az.theternal.console.designsystem.components.modifier.applyIf
import az.theternal.console.designsystem.components.provider.ThemeProvider

@Composable
fun ConsoleProvider(
    enabled: Boolean = true,
    trigger: ConsoleTrigger = ConsoleTrigger.swipeSequence(
        Swipe.UP,
        Swipe.DOWN,
        Swipe.LEFT,
        Swipe.RIGHT,
    ),
    logRenderer: LogRenderer = DefaultLogRenderer,
    content: @Composable () -> Unit,
) {
    SideEffect { Console.isEnabled = enabled }

    if (!enabled) {
        content()
        return
    }

    val backStack = remember { mutableStateListOf<NavKey>(ConsoleRoute.Stub, ConsoleRoute.Main) }
    val consoleVisible = remember { mutableStateOf(false) }
    val requestedTab = remember { mutableStateOf<ConsoleTab?>(null) }
    val navigator: ConsoleNavigator = remember { ConsoleNavigatorImpl(backStack, consoleVisible, requestedTab) }

    ThemeProvider {
        CompositionLocalProvider(
            LocalConsoleNavigator provides navigator,
            LocalLogRenderer provides logRenderer,
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .applyIf(!consoleVisible.value) {
                        with(trigger) {
                            attach(
                                onDetected = {
                                    consoleVisible.value = true
                                },
                            )
                        }
                    },
            ) {
                content()

                ConsoleOverlays.overlays.forEach { overlay -> overlay() }

                AnimatedVisibility(
                    visible = consoleVisible.value,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Box(Modifier.fillMaxSize()) {
                        ConsoleNavHost(
                            navController = navigator,
                            backStack = backStack,
                            requestedTab = requestedTab.value,
                            onRequestConsumed = { requestedTab.value = null },
                        )
                    }
                }
            }
        }
    }
}
