package az.theternal.console.compose

import androidx.compose.runtime.Composable
import az.theternal.console.api.trigger.ConsoleTrigger
import az.theternal.console.api.ui.LogRenderer
import az.theternal.console.runtime.Log

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

private object NoOpLogRenderer : LogRenderer {
    @Composable override fun Item(
        log: Log,
        onClick: () -> Unit,
    ) = Unit

    @Composable override fun Detail(
        log: Log,
        onBack: () -> Unit,
    ) = Unit
}
