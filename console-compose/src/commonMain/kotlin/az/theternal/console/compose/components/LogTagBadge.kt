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
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
fun LogTagBadge(
    tag: String?,
    color: Color,
) {
    Row(
        modifier = Modifier
            .clip(Theme.rounding.r4)
            .background(color.copy(alpha = Theme.opacity.S12))
            .padding(horizontal = Theme.dimens.dp6, vertical = Theme.dimens.dp3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4),
    ) {
        Box(
            modifier = Modifier
                .size(Theme.metrics.statusDotSize)
                .clip(CircleShape)
                .background(color),
        )
        if (tag != null) {
            DsText(
                text = tag,
                style = Theme.typography.label02,
                color = color,
            )
        }
    }
}
