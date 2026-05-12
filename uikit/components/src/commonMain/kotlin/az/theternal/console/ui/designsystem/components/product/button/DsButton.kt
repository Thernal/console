package az.theternal.console.ui.designsystem.components.product.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import az.theternal.console.ui.designsystem.components.core.DsIcon
import az.theternal.console.ui.designsystem.components.core.DsText
import az.theternal.console.ui.designsystem.components.core.Spacer
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
fun DsButton(
    onTap: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    style: DsButtonStyle = DsButtonStyle.primary,
    shape: RoundedCornerShape = Theme.rounding.r16,
    height: Dp = Theme.metrics.minTouchTarget,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(
        space = Theme.dimens.dp8,
        alignment = Alignment.CenterHorizontally,
    ),
    content: @Composable RowScope.(contentColor: androidx.compose.ui.graphics.Color) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor = if (enabled) style.content else Theme.colors.content03
    val backgroundColor = if (enabled) style.background else Theme.colors.background3
    val borderColor = if (enabled) style.border else Theme.colors.border

    Row(
        modifier = modifier
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onTap,
            )
            .defaultMinSize(minHeight = height)
            .background(color = backgroundColor, shape = shape)
            .border(width = Theme.metrics.borderWidth, color = borderColor, shape = shape)
            .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement,
    ) {
        content(contentColor)
    }
}

@Composable
fun DsButton(
    onTap: () -> Unit,
    text: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    style: DsButtonStyle = DsButtonStyle.primary,
    shape: RoundedCornerShape = Theme.rounding.r16,
    height: Dp = Theme.metrics.minTouchTarget,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(
        space = Theme.dimens.dp8,
        alignment = Alignment.CenterHorizontally,
    ),
    suffixIcon: ImageVector? = null,
    prefixIcon: ImageVector? = null,
) {
    DsButton(
        onTap = onTap,
        modifier = modifier,
        enabled = enabled,
        style = style,
        shape = shape,
        height = height,
        horizontalArrangement = horizontalArrangement,
    ) { contentColor ->
        prefixIcon?.let {
            DsIcon(
                icon = prefixIcon,
                size = Theme.metrics.iconSm,
                tint = contentColor,
            )
        } ?: suffixIcon?.let {
            Spacer(Theme.metrics.iconSm)
        }

        DsText(
            text = text,
            style = Theme.typography.label01,
            color = contentColor,
        )

        suffixIcon?.let {
            DsIcon(
                icon = suffixIcon,
                size = Theme.metrics.iconSm,
                tint = contentColor,
            )
        } ?: prefixIcon?.let {
            Spacer(Theme.metrics.iconSm)
        }
    }
}
