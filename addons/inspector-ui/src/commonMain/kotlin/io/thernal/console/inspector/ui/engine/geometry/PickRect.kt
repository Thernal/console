package io.thernal.console.inspector.ui.engine.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

/**
 * The area a touch at [point] counts as hitting for a node with this [content] box. A touch just outside the content
 * is tested against the node's [outer] box (content plus padding) so tapping a padding picks the node itself, not
 * its parent. [outer] is read lazily because it costs a layout query, and only nodes near the touch need it.
 */
internal inline fun pickRect(
    content: Rect,
    point: Offset,
    outer: () -> Rect?,
): Rect {
    if (content.contains(point)) return content
    val reach = Rect(
        content.left - MAX_PADDING_REACH_PX,
        content.top - MAX_PADDING_REACH_PX,
        content.right + MAX_PADDING_REACH_PX,
        content.bottom + MAX_PADDING_REACH_PX,
    )
    if (!reach.contains(point)) return content
    return outer()?.let { union(content, it) } ?: content
}

/** How far from a node's content box a touch may start looking for its padding, in px. */
internal const val MAX_PADDING_REACH_PX = 256f
