package az.theternal.console.debugstepper.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import az.theternal.console.ui.designsystem.components.provider.ThemeProvider
import az.theternal.console.ui.designsystem.foundation.theme.DsPreview
import az.theternal.console.ui.designsystem.components.core.DsText
import az.theternal.console.ui.designsystem.components.modifier.pressable
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
internal fun SelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val bg = if (selected) Theme.colors.primary01 else Theme.colors.background3
    val fg = if (selected) Theme.colors.primaryContent else Theme.colors.content03

    Box(
        modifier = Modifier
            .clip(Theme.rounding.r6)
            .background(bg)
            .then(
                if (!selected) {
                    Modifier.border(Theme.metrics.borderWidth, Theme.colors.border, Theme.rounding.r6)
                } else {
                    Modifier
                },
            )
            .pressable(
                onPress = onClick,
            )
            .padding(horizontal = Theme.dimens.dp10, vertical = Theme.dimens.dp8),
    ) {
        DsText(
            text = label,
            style = Theme.typography.label01,
            color = fg,
        )
    }
}

@DsPreview
@Composable
private fun PreviewSelectableChips() {
    ThemeProvider {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
            modifier = Modifier.padding(Theme.dimens.dp16),
        ) {
            SelectableChip(label = "All", selected = true, onClick = {})
            SelectableChip(label = "Info", selected = false, onClick = {})
            SelectableChip(label = "Error", selected = false, onClick = {})
        }
    }
}
