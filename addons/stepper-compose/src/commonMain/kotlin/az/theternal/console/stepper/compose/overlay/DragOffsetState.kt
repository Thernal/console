package az.theternal.console.stepper.compose.overlay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

@Stable
class DragOffsetState {
    var offsetX by mutableFloatStateOf(0f)
        private set
    var offsetY by mutableFloatStateOf(0f)
        private set
    var cardSize by mutableStateOf(IntSize.Zero)
        private set

    fun onSizeChanged(
        newSize: IntSize,
        maxWidthPx: Float,
        maxHeightPx: Float,
    ) {
        cardSize = newSize
        offsetX = offsetX.coerceIn(-(maxWidthPx - newSize.width).coerceAtLeast(0f), 0f)
        offsetY = offsetY.coerceIn(0f, (maxHeightPx - newSize.height).coerceAtLeast(0f))
    }

    fun onDrag(
        dragAmount: Offset,
        maxWidthPx: Float,
        maxHeightPx: Float,
    ) {
        offsetX = (offsetX + dragAmount.x).coerceIn(
            minimumValue = -(maxWidthPx - cardSize.width).coerceAtLeast(0f),
            maximumValue = 0f,
        )
        offsetY = (offsetY + dragAmount.y).coerceIn(
            minimumValue = 0f,
            maximumValue = (maxHeightPx - cardSize.height).coerceAtLeast(0f),
        )
    }
}

@Composable
fun rememberDragOffset(): DragOffsetState {
    return remember { DragOffsetState() }
}
