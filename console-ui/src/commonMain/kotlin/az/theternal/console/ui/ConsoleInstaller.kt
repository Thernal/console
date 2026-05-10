package az.theternal.console.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import az.theternal.console.core.Console
import az.theternal.console.ui.gesture.ConsoleTrigger
import az.theternal.console.ui.gesture.Swipe
import az.theternal.console.ui.nav.ConsoleNavHost
import az.theternal.console.ui.designsystem.ConsoleTheme

@Composable
fun ConsoleInstaller(
    enabled: Boolean = true,
    trigger: ConsoleTrigger = ConsoleTrigger.swipeSequence(Swipe.UP, Swipe.DOWN),
    content: @Composable () -> Unit,
) {
    if (!Console.isEnabled) {
        content()
        return
    }

    var consoleVisible by remember { mutableStateOf(false) }

    val gestureModifier = if (enabled && !consoleVisible) {
        with(trigger) { Modifier.attach(onDetected = { consoleVisible = true }) }
    } else {
        Modifier
    }

    Box(Modifier.fillMaxSize().then(gestureModifier)) {
        content()

        ConsoleOverlays.overlays.forEach { overlay -> overlay() }

        AnimatedVisibility(
            visible = consoleVisible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            if (consoleVisible) {
                ConsoleTheme {
                    Box(Modifier.fillMaxSize()) {
                        ConsoleNavHost(onClose = { consoleVisible = false })
                    }
                }
            }
        }
    }
}
