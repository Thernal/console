package az.theternal.console.debugstepper.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import az.theternal.console.designsystem.components.modifier.pressable
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun TagChip(
    tag: String,
    onRemove: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .clip(Theme.rounding.r6)
            .background(Theme.colors.primary01.copy(alpha = 0.12f))
            .border(
                width = Theme.metrics.borderWidth,
                color = Theme.colors.primary01.copy(alpha = 0.25f),
                shape = Theme.rounding.r6,
            )
            .padding(
                start = Theme.dimens.dp8,
                end = Theme.dimens.dp4,
                top = Theme.dimens.dp6,
                bottom = Theme.dimens.dp6,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4),
    ) {
        DsText(
            text = tag,
            style = Theme.typography.label01,
            color = Theme.colors.primary01,
        )
        Box(
            modifier = Modifier
                .size(Theme.dimens.dp16)
                .clip(Theme.rounding.r4)
                .pressable(onPress = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            DsIcon(
                icon = Icons.Outlined.Close,
                size = Theme.metrics.iconXs,
                color = Theme.colors.primary01,
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewTagChip() {
    ThemeProvider {
        Row(modifier = Modifier.padding(Theme.dimens.dp16)) {
            TagChip(tag = "NETWORK", onRemove = {})
        }
    }
}
