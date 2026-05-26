package az.theternal.console.designsystem.components.core

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
import az.theternal.console.designsystem.components.modifier.applyIf
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
fun DsChip(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    color: Color = Theme.colors.primary01,
    leading: @Composable RowScope.() -> Unit = {},
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val shape = Theme.rounding.r12

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
                horizontal = Theme.dimens.dp14,
                vertical = Theme.dimens.dp8,
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
