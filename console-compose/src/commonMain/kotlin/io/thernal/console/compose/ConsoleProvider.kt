package io.thernal.console.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation3.runtime.NavKey
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.api.navigation.ConsoleNavigator
import io.thernal.console.api.navigation.ConsoleRoute
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.api.addon.ConsoleOverlays
import io.thernal.console.api.ui.LocalLogRenderer
import io.thernal.console.api.ui.LogRenderer
import io.thernal.console.runtime.console.Console
import io.thernal.console.compose.addon.DispatchLogRenderer
import io.thernal.console.api.trigger.ConsoleTrigger
import io.thernal.console.api.ui.LogRendererRegistry
import io.thernal.console.compose.addon.BasicLogRenderer
import io.thernal.console.api.trigger.Swipe
import io.thernal.console.compose.trigger.swipeSequence
import io.thernal.console.compose.navigation.ConsoleNavHost
import io.thernal.console.compose.navigation.ConsoleNavigatorImpl
import io.thernal.console.designsystem.components.modifier.applyIf
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.runtime.log.BasicLog

@Composable
fun ConsoleProvider(
    enabled: Boolean = true,
    trigger: ConsoleTrigger = ConsoleTrigger.swipeSequence(
        Swipe.UP,
        Swipe.DOWN,
        Swipe.LEFT,
        Swipe.RIGHT,
    ),
    logRenderer: LogRenderer = BasicLogRenderer(),
    content: @Composable () -> Unit,
) {
    SideEffect { Console.isEnabled = enabled }

    LaunchedEffect(logRenderer) {
        LogRendererRegistry.register<BasicLog>(logRenderer)
    }

    if (!enabled) {
        content()
        return
    }

    val backStack = remember { mutableStateListOf<NavKey>(ConsoleRoute.Stub, ConsoleRoute.Main) }
    val consoleVisible = remember { mutableStateOf(false) }
    val requestedTab = remember { mutableStateOf<ConsoleTab?>(null) }
    val navigator: ConsoleNavigator =
        remember { ConsoleNavigatorImpl(backStack, consoleVisible, requestedTab) }

    ThemeProvider {
        CompositionLocalProvider(
            LocalConsoleNavigator provides navigator,
            LocalLogRenderer provides DispatchLogRenderer(),
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
                    Box(
                        Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                awaitPointerEventScope {
                                    while (true) {
                                        awaitPointerEvent(PointerEventPass.Initial)
                                            .changes.forEach { it.consume() }
                                    }
                                }
                            },
                    ) {
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
