package io.thernal.console.designsystem.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.color.Opacity
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.LocalDsContentColor
import io.thernal.console.designsystem.foundation.theme.Theme

private const val DISABLED_ALPHA = 0.4f

/**
 * A tinted-background + border button — no separate variants, [color] alone (default
 * [Theme.colors.primary01]) drives background tint, border, and content color via
 * [LocalDsContentColor], so `DsText`/`DsIcon` inside [content] need no explicit color.
 */
@Composable
fun DsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Theme.colors.primary01,
    enabled: Boolean = true,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Theme.dimens.dp8),
    content: @Composable RowScope.() -> Unit,
) {
    val shape = Theme.rounding.r12

    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else DISABLED_ALPHA)
            .clip(shape)
            .background(color = color.copy(alpha = Opacity.S12), shape = shape)
            .border(width = Theme.metrics.borderWidth, color = color.copy(alpha = Opacity.S35), shape = shape)
            .pressable(onPress = onClick, enabled = enabled)
            .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp12),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalDsContentColor provides color) {
            content()
        }
    }
}

@DsPreview
@Composable
private fun PreviewDsButton() {
    ThemeProvider {
        Row(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8)) {
            DsButton(onClick = {}) {
                DsText(text = "Primary", style = Theme.typography.body01)
            }
            DsButton(onClick = {}, color = Theme.colors.danger) {
                DsText(text = "Danger", style = Theme.typography.body01)
            }
        }
    }
}
