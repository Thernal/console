package io.thernal.console.designsystem.components.core.chip

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.thernal.console.designsystem.foundation.indication.PressableIndication
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.foundation.theme.Theme
import kotlinx.coroutines.delay

private const val DISABLED_ALPHA = 0.4f
private const val TOOLTIP_MS = 1800L
private val CHIP_SIZE = 32.dp
private val ICON_SIZE = 18.dp

/**
 * A compact icon-only button. [isActive] marks an on/off state: active chips are tinted and outlined, inactive ones
 * are muted, so the state reads without a second control. The name is not drawn; a long press shows it in a
 * tooltip, and it is also the accessibility description.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DsActionChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    isEnabled: Boolean = true,
) {
    val color = if (isActive) Theme.colors.primary01 else Theme.colors.content03
    val shape = Theme.rounding.r8
    val haptics = LocalHapticFeedback.current
    var isTooltipShown by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(CHIP_SIZE)
            .alpha(if (isEnabled) 1f else DISABLED_ALPHA)
            .clip(shape)
            .background(color.copy(alpha = Theme.opacity.S12), shape)
            .then(
                if (isActive) {
                    Modifier.border(Theme.metrics.borderWidth, color.copy(alpha = Theme.opacity.S65), shape)
                } else {
                    Modifier
                },
            )
            .semantics { contentDescription = label }
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = PressableIndication(),
                onClick = onClick,
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    isTooltipShown = true
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        DsIcon(icon = icon, size = ICON_SIZE, color = color)
        if (isTooltipShown) {
            LaunchedEffect(Unit) {
                delay(TOOLTIP_MS)
                isTooltipShown = false
            }
            DsTooltip(text = label, onDismiss = { isTooltipShown = false })
        }
    }
}
