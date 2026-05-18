package az.theternal.console.compose.renderer.item.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import az.theternal.console.designsystem.components.modifier.pressable
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun LogItemPill(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    iconModifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .clip(Theme.rounding.r6)
            .background(Theme.colors.background3)
            .pressable(
                onPress = onClick,
            )
            .padding(horizontal = Theme.dimens.dp8, vertical = Theme.dimens.dp3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp3),
    ) {
        DsIcon(
            icon = icon,
            size = Theme.metrics.iconXs,
            color = Theme.colors.content03,
            modifier = iconModifier,
        )
        DsText(
            text = label,
            style = Theme.typography.label02,
            color = Theme.colors.content03,
        )
    }
}
