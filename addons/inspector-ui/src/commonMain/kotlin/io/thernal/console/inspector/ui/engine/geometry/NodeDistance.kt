package io.thernal.console.inspector.ui.engine.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** A measured stretch between two points, in window pixels. */
internal data class DistanceSegment(
    val from: Offset,
    val to: Offset,
) {
    val length: Float get() = max(abs(to.x - from.x), abs(to.y - from.y))
    val center: Offset get() = Offset((from.x + to.x) / 2, (from.y + to.y) / 2)
}

/**
 * How two rectangles relate. [segments] are the stretches to draw, [horizontal] and [vertical] the gaps between
 * their facing edges (zero where they overlap on that axis), and [isNested] is true when one holds the other, in
 * which case the segments are the four insets instead.
 */
internal data class NodeDistance(
    val segments: List<DistanceSegment>,
    val horizontal: Float,
    val vertical: Float,
    val isNested: Boolean,
    val isOverlapping: Boolean = false,
) {
    /** Short text for the dock, in dp: `↔ 24 · ↕ 12 dp`, `Inset 8 · 8 · 12 · 12 dp`, or `Overlapping`. */
    fun describe(density: Float): String {
        fun dp(px: Float): Int {
            return (px / density).roundToInt()
        }
        return when {
            isNested -> "Inset " + segments.joinToString(" · ") { dp(it.length).toString() } + " dp"
            isOverlapping -> "Overlapping"
            horizontal > 0f && vertical > 0f -> "↔ ${dp(horizontal)} · ↕ ${dp(vertical)} dp"
            horizontal > 0f -> "↔ ${dp(horizontal)} dp"
            vertical > 0f -> "↕ ${dp(vertical)} dp"
            else -> "Touching · 0 dp"
        }
    }
}

/** Measures how far [a] is from [b]: gaps between facing edges, or the insets when one contains the other. */
internal fun measureDistance(
    a: Rect,
    b: Rect,
): NodeDistance {
    val nestedPair = when {
        a.contains(b) -> b to a
        b.contains(a) -> a to b
        else -> null
    }
    if (nestedPair != null) {
        return NodeDistance(insetSegments(inner = nestedPair.first, outer = nestedPair.second), 0f, 0f, isNested = true)
    }

    val overlapX = min(a.right, b.right) - max(a.left, b.left)
    val overlapY = min(a.bottom, b.bottom) - max(a.top, b.top)
    if (overlapX > EDGE_TOLERANCE_PX && overlapY > EDGE_TOLERANCE_PX) {
        return NodeDistance(emptyList(), horizontal = 0f, vertical = 0f, isNested = false, isOverlapping = true)
    }
    val horizontal = gap(overlapX)
    val vertical = gap(overlapY)
    val segments = buildList {
        if (horizontal > 0f) add(horizontalSegment(a, b))
        if (vertical > 0f) add(verticalSegment(a, b, hasHorizontalGap = horizontal > 0f))
    }
    return NodeDistance(segments, horizontal, vertical, isNested = false)
}

/**
 * Left, right, top, bottom, each measured from the middle of the [inner] edge to the [outer] one. Stretches under a
 * pixel are dropped.
 */
internal fun insetSegments(
    inner: Rect,
    outer: Rect,
): List<DistanceSegment> {
    val midX = inner.center.x
    val midY = inner.center.y
    return listOf(
        DistanceSegment(Offset(outer.left, midY), Offset(inner.left, midY)),
        DistanceSegment(Offset(inner.right, midY), Offset(outer.right, midY)),
        DistanceSegment(Offset(midX, outer.top), Offset(midX, inner.top)),
        DistanceSegment(Offset(midX, inner.bottom), Offset(midX, outer.bottom)),
    ).filter { it.length >= 1f }
}

private fun Rect.contains(other: Rect): Boolean =
    left <= other.left + EDGE_TOLERANCE_PX && top <= other.top + EDGE_TOLERANCE_PX &&
        right >= other.right - EDGE_TOLERANCE_PX && bottom >= other.bottom - EDGE_TOLERANCE_PX

/** The space between two intervals given their overlap (negative when apart). Under half a pixel counts as none. */
private fun gap(overlap: Float): Float {
    return (-overlap).takeIf { it >= EDGE_TOLERANCE_PX } ?: 0f
}

private fun horizontalSegment(
    a: Rect,
    b: Rect,
): DistanceSegment {
    val isBRight = a.right <= b.left
    val fromX = if (isBRight) a.right else a.left
    val toX = if (isBRight) b.left else b.right
    val overlapTop = max(a.top, b.top)
    val overlapBottom = min(a.bottom, b.bottom)
    val y = if (overlapTop < overlapBottom) {
        (overlapTop + overlapBottom) / 2
    } else {
        if (b.top >= a.bottom) a.bottom else a.top
    }
    return DistanceSegment(Offset(fromX, y), Offset(toX, y))
}

private fun verticalSegment(
    a: Rect,
    b: Rect,
    hasHorizontalGap: Boolean,
): DistanceSegment {
    val isBBelow = a.bottom <= b.top
    val fromY = if (isBBelow) a.bottom else a.top
    val toY = if (isBBelow) b.top else b.bottom
    val overlapLeft = max(a.left, b.left)
    val overlapRight = min(a.right, b.right)
    val x = if (!hasHorizontalGap && overlapLeft < overlapRight) {
        (overlapLeft + overlapRight) / 2
    } else {
        if (b.left >= a.right) b.left else b.right
    }
    return DistanceSegment(Offset(x, fromY), Offset(x, toY))
}

private const val EDGE_TOLERANCE_PX = 0.5f
