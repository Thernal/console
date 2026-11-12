package io.thernal.console.ui.dock

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import io.thernal.console.ui.core.IntentHandler
import io.thernal.console.ui.core.StateHolder

internal class DockViewModel : ViewModel(), StateHolder, IntentHandler<DockIntent> {
    val state = DockState()

    override val handler = onIntentUpdate { intent ->
        when (intent) {
            is DockIntent.SelectWidget -> state.selectedId.set(intent.widgetId)

            is DockIntent.SetMinimized -> state.isMinimized.set(intent.isMinimized)

            is DockIntent.SizeChanged -> resize(intent.newSize, intent.maxWidthPx, intent.maxHeightPx)

            is DockIntent.MoveTo -> placeAt(
                offset = offsetFor(
                    edge = intent.edge,
                    verticalFraction = intent.vertical.fraction,
                    size = state.size.value,
                    maxWidth = intent.maxWidthPx,
                    maxHeight = intent.maxHeightPx,
                ),
                size = state.size.value,
                maxWidthPx = intent.maxWidthPx,
                maxHeightPx = intent.maxHeightPx,
            )

            DockIntent.DragStarted -> state.isDragging.set(true)

            is DockIntent.Dragged -> moveWithin(
                dx = intent.dragAmount.x,
                dy = intent.dragAmount.y,
                size = state.size.value,
                maxWidthPx = intent.maxWidthPx,
                maxHeightPx = intent.maxHeightPx,
            )

            is DockIntent.DragEnded -> finishDrag(intent.maxWidthPx, intent.snapToEdge)
        }
    }

    /** The dock is anchored to the top end, so x only moves left and y only moves down. */
    private fun moveWithin(
        dx: Float,
        dy: Float,
        size: IntSize,
        maxWidthPx: Float,
        maxHeightPx: Float,
    ) {
        placeAt(Offset(state.offsetX.value + dx, state.offsetY.value + dy), size, maxWidthPx, maxHeightPx)
    }

    private fun resize(
        size: IntSize,
        maxWidthPx: Float,
        maxHeightPx: Float,
    ) {
        val current = Offset(state.offsetX.value, state.offsetY.value)
        val offset = resizedOffset(current, state.size.value, size, maxWidthPx, maxHeightPx)
        placeAt(offset, size, maxWidthPx, maxHeightPx)
    }

    private fun placeAt(
        offset: Offset,
        size: IntSize,
        maxWidthPx: Float,
        maxHeightPx: Float,
    ) {
        snapshot {
            state.size.set(size)
            state.offsetX.set(offset.x.coerceIn(-travel(maxWidthPx, size.width), 0f))
            state.offsetY.set(offset.y.coerceIn(0f, travel(maxHeightPx, size.height)))
        }
    }

    private fun finishDrag(
        maxWidthPx: Float,
        snapToEdge: Boolean,
    ) {
        snapshot {
            state.isDragging.set(false)
            if (snapToEdge) {
                state.offsetX.set(
                    snapToHorizontalEdge(
                        offsetX = state.offsetX.value,
                        width = state.size.value.width.toFloat(),
                        maxWidth = maxWidthPx,
                    ),
                )
            }
        }
    }
}
