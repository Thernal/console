package io.thernal.console.ui.dock

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

            is DockIntent.SizeChanged -> moveWithin(0f, 0f, intent.newSize, intent.maxWidthPx, intent.maxHeightPx)

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
        snapshot {
            state.size.set(size)
            state.offsetX.set((state.offsetX.value + dx).coerceIn(-(maxWidthPx - size.width).coerceAtLeast(0f), 0f))
            state.offsetY.set((state.offsetY.value + dy).coerceIn(0f, (maxHeightPx - size.height).coerceAtLeast(0f)))
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
