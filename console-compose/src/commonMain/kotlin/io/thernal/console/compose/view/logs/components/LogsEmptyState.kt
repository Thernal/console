package io.thernal.console.compose.view.logs.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun LogsEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
        ) {
            DsIcon(
                icon = Icons.AutoMirrored.Outlined.List,
                size = Theme.dimens.dp32,
                color = Theme.colors.content04,
            )
            DsText(
                text = "No logs yet",
                style = Theme.typography.body02,
                color = Theme.colors.content03,
            )
            DsText(
                text = "Logs will appear here as they arrive",
                style = Theme.typography.body03,
                color = Theme.colors.content04,
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewLogsEmptyState() {
    ThemeProvider {
        LogsEmptyState()
    }
}
