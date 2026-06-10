package io.thernal.console.logging.ui.view.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun LogNotFoundState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
        ) {
            DsIcon(
                icon = Icons.Outlined.SearchOff,
                size = Theme.dimens.dp32,
                color = Theme.colors.content04,
            )
            DsText(
                text = "Log not found",
                style = Theme.typography.body02,
                color = Theme.colors.content03,
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewLogNotFoundState() {
    ThemeProvider {
        LogNotFoundState()
    }
}
