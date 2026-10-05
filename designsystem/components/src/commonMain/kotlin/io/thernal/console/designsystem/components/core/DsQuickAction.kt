package io.thernal.console.designsystem.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.foundation.theme.Theme

private val actionSize = 32.dp
private const val DISABLED_ALPHA = 0.4f

/** A small square icon button. The primary one is filled, so the likeliest next step stands out. */
@Composable
fun DsQuickAction(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    isEnabled: Boolean = true,
) {
    val fill = if (isPrimary) Theme.colors.primary01 else Theme.colors.content01.copy(alpha = Theme.opacity.S12)
    val content = if (isPrimary) Theme.colors.primaryContent else Theme.colors.content02
    Box(
        modifier = modifier
            .size(actionSize)
            .alpha(if (isEnabled) 1f else DISABLED_ALPHA)
            .clip(Theme.rounding.r8)
            .background(fill)
            .pressable(enabled = isEnabled, onPress = onClick),
        contentAlignment = Alignment.Center,
    ) {
        DsIcon(icon = icon, size = Theme.metrics.iconSm, color = content)
    }
}
