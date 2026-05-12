package az.theternal.console.ui.designsystem.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
fun DsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = Theme.colors.primary01,
    contentColor: Color = Theme.colors.primaryContent,
    contentPadding: PaddingValues = PaddingValues(horizontal = Theme.dimens.dp12, vertical = Theme.dimens.dp8),
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val bg = if (enabled) containerColor else Theme.colors.content04
    val fg = if (enabled) contentColor else Theme.colors.content02

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = Theme.metrics.minTouchTarget)
            .background(bg, Theme.rounding.r8)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(contentPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(
            LocalDsContentColor provides fg,
            LocalDsTextStyle provides Theme.typography.label01,
        ) {
            content()
        }
    }
}

@Composable
fun DsOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = Theme.colors.border,
    contentColor: Color = Theme.colors.content01,
    contentPadding: PaddingValues = PaddingValues(horizontal = Theme.dimens.dp12, vertical = Theme.dimens.dp8),
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val border = if (enabled) borderColor else Theme.colors.content04
    val fg = if (enabled) contentColor else Theme.colors.content03

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = Theme.metrics.minTouchTarget)
            .border(Theme.metrics.borderWidth, border, Theme.rounding.r8)
            .background(Theme.colors.background2, Theme.rounding.r8)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(contentPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(
            LocalDsContentColor provides fg,
            LocalDsTextStyle provides Theme.typography.label01,
        ) {
            content()
        }
    }
}
