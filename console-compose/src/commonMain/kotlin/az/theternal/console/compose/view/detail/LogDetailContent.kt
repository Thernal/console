package az.theternal.console.compose.view.detail

import androidx.compose.runtime.Composable
import az.theternal.console.api.ui.LogRenderer
import az.theternal.console.compose.renderer.defaultLogRenderer
import az.theternal.console.compose.view.detail.components.LogNotFoundState
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.runtime.Log
import az.theternal.console.runtime.LogLevel

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

@DsPreview
@Composable
private fun PreviewLogDetailContentNotFound() {
    ThemeProvider {
        LogDetailContent(log = null, onBack = {}, renderer = defaultLogRenderer())
    }
}

@DsPreview
@Composable
private fun PreviewLogDetailContentFound() {
    ThemeProvider {
        LogDetailContent(
            log = Log(message = "Network request completed in 342ms", tag = "HTTP", level = LogLevel.Info),
            onBack = {},
            renderer = defaultLogRenderer(),
        )
    }
}
