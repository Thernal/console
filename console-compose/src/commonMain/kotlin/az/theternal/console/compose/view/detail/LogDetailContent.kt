package az.theternal.console.compose.view.detail

import androidx.compose.runtime.Composable
import az.theternal.console.api.ui.LogRenderer
import az.theternal.console.compose.view.detail.components.LogNotFoundState
import az.theternal.console.runtime.Log

@Composable
internal fun LogDetailContent(
    log: Log?,
    onBack: () -> Unit,
    renderer: LogRenderer,
) {
    log?.let {
        renderer.Detail(log = it, onBack = onBack)
    } ?: LogNotFoundState()
}
