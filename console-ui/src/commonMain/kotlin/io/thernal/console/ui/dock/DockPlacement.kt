package io.thernal.console.ui.dock

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import io.thernal.console.api.addon.ConsoleDockEdge

private const val FLUSH_TOLERANCE_PX = 1f

/**
 * The offsets that put a box of [size] flush with [edge], [verticalFraction] of the way down. The dock is anchored to
 * the top end, so x runs from `-(maxWidth - width)` (left edge) to `0` and y from `0` to `maxHeight - height`.
 */
internal fun offsetFor(
    edge: ConsoleDockEdge,
    verticalFraction: Float,
    size: IntSize,
    maxWidth: Float,
    maxHeight: Float,
): Offset {
    val x = if (edge == ConsoleDockEdge.Left) -travel(maxWidth, size.width) else 0f
    return Offset(x = x, y = travel(maxHeight, size.height) * verticalFraction.coerceIn(0f, 1f))
}

/**
 * The offsets for a box that grows or shrinks from [oldSize] to [newSize]: one flush with the left or the bottom edge
 * stays flush with it, so a dock moved there and then folded or opened does not drift off the edge. The right and top
 * edges hold by themselves, since the dock is anchored to the top end.
 */
internal fun resizedOffset(
    offset: Offset,
    oldSize: IntSize,
    newSize: IntSize,
    maxWidth: Float,
    maxHeight: Float,
): Offset {
    val oldTravelX = travel(maxWidth, oldSize.width)
    val oldTravelY = travel(maxHeight, oldSize.height)
    val isFlushLeft = offset.x < 0f && offset.x <= -oldTravelX + FLUSH_TOLERANCE_PX
    val isFlushBottom = offset.y > 0f && offset.y >= oldTravelY - FLUSH_TOLERANCE_PX
    val x = if (isFlushLeft) -travel(maxWidth, newSize.width) else offset.x
    val y = if (isFlushBottom) travel(maxHeight, newSize.height) else offset.y
    return Offset(x = x, y = y)
}

/** How far a box of [length] can move along a side of [maxLength]. */
internal fun travel(
    maxLength: Float,
    length: Int,
): Float {
    return (maxLength - length).coerceAtLeast(0f)
}
