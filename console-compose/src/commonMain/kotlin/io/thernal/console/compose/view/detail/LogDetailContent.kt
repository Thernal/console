package io.thernal.console.compose.view.detail

import androidx.compose.runtime.Composable
import io.thernal.console.api.ui.LogRenderer
import io.thernal.console.compose.components.defaultLogRenderer
import io.thernal.console.compose.view.detail.components.LogNotFoundState
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.runtime.Log
import io.thernal.console.runtime.LogLevel

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
