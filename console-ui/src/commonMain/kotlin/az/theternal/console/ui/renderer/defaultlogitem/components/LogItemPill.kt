package az.theternal.console.ui.renderer.defaultlogitem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import az.theternal.console.ui.designsystem.components.core.DsIcon
import az.theternal.console.ui.designsystem.components.core.DsText
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
internal fun LogItemPill(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    iconModifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .clip(Theme.rounding.r6)
            .background(Theme.colors.background3)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = Theme.dimens.dp8, vertical = Theme.dimens.dp3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp3),
    ) {
        DsIcon(
            icon = icon,
            size = Theme.metrics.iconXs,
            tint = Theme.colors.content03,
            modifier = iconModifier,
        )
        DsText(
            text = label,
            style = Theme.typography.label02,
            color = Theme.colors.content03,
        )
    }
}
