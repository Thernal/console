package az.theternal.console.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
fun LogTagBadge(
    tag: String?,
    color: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4),
    ) {
        Box(
            modifier = Modifier
                .size(Theme.dimens.dp6)
                .clip(CircleShape)
                .background(color),
        )
        if (tag != null) {
            DsText(
                text = tag,
                style = Theme.typography.label01,
                color = color,
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewLogTagBadge() {
    ThemeProvider {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
            modifier = Modifier.padding(Theme.dimens.dp16),
        ) {
            LogTagBadge(tag = "HTTP", color = Theme.colors.primary01)
            LogTagBadge(tag = "AUTH", color = Theme.colors.success)
            LogTagBadge(tag = null, color = Theme.colors.content03)
        }
    }
}
