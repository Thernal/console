package az.theternal.console.compose.renderer.detail.components.metacard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun MetaRow(
    label: String,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.dimens.dp12, vertical = Theme.dimens.dp10),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp16),
    ) {
        DsText(
            text = label,
            style = Theme.typography.label02,
            color = Theme.colors.content04,
            modifier = Modifier.width(Theme.dimens.dp40),
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd,
        ) {
            content()
        }
    }
}

@DsPreview
@Composable
private fun PreviewMetaRow() {
    ThemeProvider {
        MetaRow(label = "Tag") {
            DsText(
                text = "HTTP",
                style = Theme.typography.body02,
                color = Theme.colors.content01,
            )
        }
    }
}
