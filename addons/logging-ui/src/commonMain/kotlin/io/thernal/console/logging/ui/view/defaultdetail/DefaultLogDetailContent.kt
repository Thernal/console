package io.thernal.console.logging.ui.view.defaultdetail

import androidx.compose.runtime.Composable
import io.thernal.console.ui.common.logAccentColor
import io.thernal.console.logging.ui.view.defaultdetail.components.MessageCard
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.runtime.log.Log
import io.thernal.console.runtime.log.LogLevel

@Composable
internal fun DefaultLogDetailContent(
    log: Log,
    onCopy: () -> Unit,
) {
    MessageCard(
        log = log,
        accentColor = log.logAccentColor(),
        onCopy = onCopy,
    )
}

@DsPreview
@Composable
private fun PreviewDefaultLogDetailContent() {
    ThemeProvider {
        DefaultLogDetailContent(
            log = Log(
                message = "Response received\nBody: {\"status\":\"ok\",\"userId\":42}",
                tag = "API",
                level = LogLevel.Info,
            ),
            onCopy = {},
        )
    }
}
