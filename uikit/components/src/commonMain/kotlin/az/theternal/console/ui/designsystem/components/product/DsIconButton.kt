package az.theternal.console.ui.designsystem.components.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import az.theternal.console.ui.designsystem.components.core.DsIconButton as CoreIconButton
import az.theternal.console.ui.designsystem.components.core.Spacer
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
fun DsIconButton(
    alignment: Alignment = Alignment.Center,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = Theme.metrics.minTouchTarget,
    icon: @Composable () -> Unit,
) {
    CoreIconButton(
        onClick = onTap,
        modifier = modifier.size(size),
        content = {
            Box(contentAlignment = alignment) {
                icon()
            }
        },
    )
}

@Composable
fun DsIconButton(
    label: (@Composable RowScope.() -> Unit)? = null,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = Theme.metrics.minTouchTarget,
    spacing: Dp = Theme.dimens.dp4,
    padding: PaddingValues = PaddingValues(),
    icon: @Composable RowScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onTap,
            )
            .defaultMinSize(minHeight = height)
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()

        label?.let {
            Spacer(spacing)
            label()
        }
    }
}
