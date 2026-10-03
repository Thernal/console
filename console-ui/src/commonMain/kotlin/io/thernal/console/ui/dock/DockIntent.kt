package io.thernal.console.ui.dock

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import io.thernal.console.ui.core.ViewIntent

internal sealed interface DockIntent : ViewIntent {
    data class SelectWidget(val widgetId: String) : DockIntent

    data class SetMinimized(val isMinimized: Boolean) : DockIntent

    data class SizeChanged(
        val newSize: IntSize,
        val maxWidthPx: Float,
        val maxHeightPx: Float,
    ) : DockIntent

    data object DragStarted : DockIntent

    data class Dragged(
        val dragAmount: Offset,
        val maxWidthPx: Float,
        val maxHeightPx: Float,
    ) : DockIntent

    data class DragEnded(
        val maxWidthPx: Float,
        val snapToEdge: Boolean,
    ) : DockIntent
}
