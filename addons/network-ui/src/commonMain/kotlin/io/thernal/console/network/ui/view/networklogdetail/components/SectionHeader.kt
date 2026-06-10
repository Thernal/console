package io.thernal.console.network.ui.view.networklogdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.core.chip.DsChipSize
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun SectionHeader(
    title: String,
    badge: String?,
    expanded: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressable(onPress = onClick)
            .padding(
                horizontal = Theme.dimens.dp12,
                vertical = Theme.dimens.dp10,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = Theme.dimens.dp8,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DsText(
                text = title,
                style = Theme.typography.label01,
                color = Theme.colors.content01,
            )

            badge?.let {
                DsChip(label = it, color = accentColor, size = DsChipSize.Small)
            }
        }

        DsIcon(
            icon = if (expanded) {
                Icons.Outlined.KeyboardArrowUp
            } else {
                Icons.Outlined.KeyboardArrowDown
            },
            color = Theme.colors.content03,
        )
    }
}
