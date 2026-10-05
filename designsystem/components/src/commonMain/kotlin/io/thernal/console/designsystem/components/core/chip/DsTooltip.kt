package io.thernal.console.designsystem.components.core.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme

private val gap = 6.dp

/** Centers above its anchor and flips below when there is no room, staying inside the window. */
private object TooltipPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val x = (anchorBounds.center.x - popupContentSize.width / 2)
            .coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0))
        val above = anchorBounds.top - popupContentSize.height
        val y = if (above >= 0) above else anchorBounds.bottom
        return IntOffset(x, y)
    }
}

/** A small label bubble anchored to the composable it is placed in. */
@Composable
internal fun DsTooltip(
    text: String,
    onDismiss: () -> Unit,
) {
    Popup(popupPositionProvider = TooltipPositionProvider, onDismissRequest = onDismiss) {
        Box(
            Modifier
                .padding(gap)
                .background(Theme.colors.background3, Theme.rounding.r6)
                .border(Theme.metrics.borderWidth, Theme.colors.border, Theme.rounding.r6)
                .padding(horizontal = Theme.dimens.dp8, vertical = Theme.dimens.dp4),
        ) {
            DsText(text = text, style = Theme.typography.label02, color = Theme.colors.content01)
        }
    }
}
