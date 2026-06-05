package io.thernal.console.designsystem.components.core.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.modifier.applyIf
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
fun DsChip(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean = false,
    color: Color = Theme.colors.primary01,
    size: DsChipSize = DsChipSize.Medium,
    leading: @Composable RowScope.() -> Unit = {},
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val shape = when (size) {
        DsChipSize.Small -> Theme.rounding.r8
        DsChipSize.Medium -> Theme.rounding.r12
    }

    Row(
        modifier = modifier
            .background(
                color = color.copy(alpha = Theme.opacity.S12),
                shape = shape,
            )
            .applyIf(selected) {
                border(
                    width = Theme.metrics.borderWidth,
                    color = color.copy(alpha = Theme.opacity.S65),
                    shape = shape,
                )
            }
            .padding(
                paddingValues = size.padding,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading()

        DsText(
            text = label,
            style = Theme.typography.label01,
            color = color,
        )

        trailing()
    }
}

@DsPreview
@Composable
private fun PreviewDsChip() {
    ThemeProvider {
        Row(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8)) {
            DsChip(label = "All", selected = true)
            DsChip(label = "Error", selected = false)
        }
    }
}
