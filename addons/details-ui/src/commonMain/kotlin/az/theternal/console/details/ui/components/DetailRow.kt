package az.theternal.console.details.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun DetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.dimens.dp12, vertical = Theme.dimens.dp10),
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp16),
        verticalAlignment = Alignment.Top,
    ) {
        DsText(
            text = label,
            style = Theme.typography.label02,
            color = Theme.colors.content04,
            modifier = Modifier.weight(0.38f),
        )
        Box(
            modifier = Modifier.weight(0.62f),
            contentAlignment = Alignment.TopEnd,
        ) {
            DsText(
                text = value,
                style = Theme.typography.body02,
                color = Theme.colors.content01,
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewDetailRow() {
    ThemeProvider {
        DetailRow(label = "User ID", value = "abc-123-def-456")
    }
}

@DsPreview
@Composable
private fun PreviewDetailRowLong() {
    ThemeProvider {
        DetailRow(label = "Base URL", value = "https://api.example.com/v1/users/profile")
    }
}
