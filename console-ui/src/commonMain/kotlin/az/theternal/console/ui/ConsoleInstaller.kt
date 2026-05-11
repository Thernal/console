package az.theternal.console.ui

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
import az.theternal.console.core.Console
import az.theternal.console.ui.designsystem.ConsoleTheme
import az.theternal.console.ui.gesture.ConsoleTrigger
import az.theternal.console.ui.gesture.Swipe
import az.theternal.console.ui.nav.ConsoleNavigatorImpl
import az.theternal.console.ui.nav.ConsoleNavHost

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
    val requestedTabTitleState = remember { mutableStateOf<String?>(null) }
    val navController = remember { ConsoleNavigatorImpl(consoleVisibleState, requestedTabTitleState) }

    val consoleVisible by consoleVisibleState
    val requestedTabTitle by requestedTabTitleState

    val gestureModifier = if (!consoleVisible) {
        with(trigger) { Modifier.attach(onDetected = { consoleVisibleState.value = true }) }
    } else {
        Modifier
    }

    CompositionLocalProvider(
        LocalConsoleNavigator provides navController,
        LocalLogRenderer provides logRenderer,
    ) {
        Box(Modifier.fillMaxSize().then(gestureModifier)) {
            content()

            ConsoleOverlays.overlays.forEach { overlay -> overlay() }

            AnimatedVisibility(
                visible = consoleVisible,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                ConsoleTheme {
                    Box(Modifier.fillMaxSize()) {
                        ConsoleNavHost(
                            navController = navController,
                            requestedTabTitle = requestedTabTitle,
                            onRequestConsumed = { requestedTabTitleState.value = null },
                        )
                    }
                }
            }
        }
    }
}
