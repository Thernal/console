package io.thernal.console.compose

import androidx.compose.runtime.Composable
import io.thernal.console.api.trigger.ConsoleTrigger
import io.thernal.console.api.ui.LogRenderer
import io.thernal.console.api.ui.NoOpLogRenderer

@Suppress("UnusedParameter")
@Composable
fun ConsoleProvider(
    enabled: Boolean = true,
    trigger: ConsoleTrigger = ConsoleTrigger { this },
    logRenderer: LogRenderer = NoOpLogRenderer,
    content: @Composable () -> Unit,
) {
    content()
}
