package az.theternal.console.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import az.theternal.console.core.Console
import az.theternal.console.ui.gesture.ConsoleTrigger
import az.theternal.console.ui.gesture.Swipe
import az.theternal.console.ui.nav.ConsoleNavHost

/**
 * Console installer — wraps your root content, renders persistent overlays
 * registered via [ConsoleOverlays], and opens the console nav on the
 * configured gesture sequence.
 *
 * Usage:
 *   @Composable
 *   fun App() {
 *       ConsoleInstaller {
 *           YourContent()
 *       }
 *   }
 *
 * Custom trigger:
 *   ConsoleInstaller(
 *       trigger = ConsoleTrigger.swipeSequence(Swipe.UP, Swipe.DOWN, Swipe.UP)
 *   ) {
 *       YourContent()
 *   }
 */
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

        // Persistent overlays contributed by other libs (e.g. DebugStepper floating card).
        ConsoleOverlays.overlays.forEach { overlay -> overlay() }
    }

    if (consoleVisible) {
        Dialog(
            onDismissRequest = { consoleVisible = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
            ),
        ) {
            Box(Modifier.fillMaxSize()) {
                ConsoleNavHost(onClose = { consoleVisible = false })
            }
        }
    }
}
