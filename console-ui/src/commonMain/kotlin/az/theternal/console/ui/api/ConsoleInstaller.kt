package az.theternal.console.ui.api

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import az.theternal.console.runtime.api.Console
import az.theternal.console.ui.api.gesture.ConsoleTrigger
import az.theternal.console.ui.api.gesture.Swipe
import az.theternal.console.ui.designsystem.components.provider.ThemeProvider
import az.theternal.console.ui.nav.ConsoleNavHost
import az.theternal.console.ui.nav.ConsoleNavigatorImpl
import az.theternal.console.ui.nav.LocalConsoleNavigator
import az.theternal.console.ui.nav.ConsoleTab
import az.theternal.console.ui.overlay.ConsoleOverlays
import az.theternal.console.ui.renderer.LocalLogRenderer
import az.theternal.console.ui.renderer.LogRenderer

@Composable
fun ConsoleInstaller(
    enabled: Boolean = true,
    trigger: ConsoleTrigger = ConsoleTrigger.swipeSequence(Swipe.UP, Swipe.DOWN),
    logRenderer: LogRenderer = DefaultLogRenderer,
    content: @Composable () -> Unit,
) {
    SideEffect { Console.isEnabled = enabled }

    if (!enabled) {
        content()
        return
    }

    val consoleVisibleState = remember { mutableStateOf(false) }
    val requestedTabState = remember { mutableStateOf<ConsoleTab?>(null) }
    val navController =
        remember { ConsoleNavigatorImpl(consoleVisibleState, requestedTabState) }

    val consoleVisible by consoleVisibleState
    val requestedTab by requestedTabState

    val gestureModifier = if (!consoleVisible) {
        with(trigger) { Modifier.attach(onDetected = { consoleVisibleState.value = true }) }
    } else {
        Modifier
    }

    ThemeProvider {
        CompositionLocalProvider(
            LocalConsoleNavigator provides navController,
            LocalLogRenderer provides logRenderer,
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .then(gestureModifier),
            ) {
                content()

                ConsoleOverlays.overlays.forEach { overlay -> overlay() }

                AnimatedVisibility(
                    visible = consoleVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Box(Modifier.fillMaxSize()) {
                        ConsoleNavHost(
                            navController = navController,
                            requestedTab = requestedTab,
                            onRequestConsumed = { requestedTabState.value = null },
                        )
                    }
                }
            }
        }
    }
}
