package io.thernal.console.ui.dock

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import io.thernal.console.api.addon.ConsoleDockEdge
import io.thernal.console.api.addon.ConsoleDockLayout
import kotlin.math.roundToInt

/**
 * The layout the dock reports through `ConsoleDock.layout`, from where [state] is heading rather than where an
 * animation has it, inside an area of [maxWidth] by [maxHeight] whose top left corner is [origin] in the window.
 * Null until the dock has been measured.
 */
internal fun dockLayout(
    state: DockState,
    selectedWidgetId: String?,
    maxWidth: Float,
    maxHeight: Float,
    origin: Offset,
): ConsoleDockLayout? {
    val size = state.size.value
    if (size == IntSize.Zero) return null
    val offsetX = state.offsetX.value
    val offsetY = state.offsetY.value
    val travelX = travel(maxWidth, size.width)
    val travelY = travel(maxHeight, size.height)
    val snapped = snapToHorizontalEdge(offsetX = offsetX, width = size.width.toFloat(), maxWidth = maxWidth)
    return ConsoleDockLayout(
        edge = if (snapped == 0f) ConsoleDockEdge.Right else ConsoleDockEdge.Left,
        verticalFraction = if (travelY > 0f) (offsetY / travelY).coerceIn(0f, 1f) else 0f,
        isMinimized = state.isMinimized.value,
        selectedWidgetId = selectedWidgetId,
        left = (origin.x + travelX + offsetX).roundToInt(),
        top = (origin.y + offsetY).roundToInt(),
        width = size.width,
        height = size.height,
    )
}
