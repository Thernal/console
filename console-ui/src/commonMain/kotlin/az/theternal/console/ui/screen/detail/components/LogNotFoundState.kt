package az.theternal.console.ui.screen.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.ui.designsystem.components.core.DsIcon
import az.theternal.console.ui.designsystem.components.core.DsText
import az.theternal.console.ui.designsystem.foundation.theme.Theme

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
                tint = Theme.colors.content04,
            )
            DsText(
                text = "Log not found",
                style = Theme.typography.body02,
                color = Theme.colors.content03,
            )
        }
    }
}
