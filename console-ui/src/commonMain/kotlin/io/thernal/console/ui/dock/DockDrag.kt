package io.thernal.console.ui.dock

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

/** Drags the dock. With [snapToEdge] the dock settles against the nearer side when released. */
internal fun Modifier.dockDrag(
    viewModel: DockViewModel,
    maxWidthPx: Float,
    maxHeightPx: Float,
    snapToEdge: Boolean,
): Modifier = pointerInput(snapToEdge, maxWidthPx, maxHeightPx) {
    detectDragGestures(
        onDragStart = { viewModel.dispatch(DockIntent.DragStarted) },
        onDragEnd = { viewModel.dispatch(DockIntent.DragEnded(maxWidthPx, snapToEdge)) },
        onDragCancel = { viewModel.dispatch(DockIntent.DragEnded(maxWidthPx, snapToEdge)) },
    ) { change, dragAmount ->
        change.consume()
        viewModel.dispatch(DockIntent.Dragged(dragAmount, maxWidthPx, maxHeightPx))
    }
}
