package io.thernal.console.ui.dock

/**
 * The offset that puts a box of [width] flush with the nearer horizontal edge. The dock is anchored to the
 * right edge, so offsets run from `-(maxWidth - width)` (left edge) to `0` (right edge).
 */
internal fun snapToHorizontalEdge(
    offsetX: Float,
    width: Float,
    maxWidth: Float,
): Float {
    val travel = (maxWidth - width).coerceAtLeast(0f)
    val centerX = travel + offsetX + width / 2f
    return if (centerX >= maxWidth / 2f) 0f else -travel
}
