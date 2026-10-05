package io.thernal.console.inspector.ui.engine.geometry

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutInfo
import androidx.compose.ui.layout.boundsInWindow

/** Smallest rect containing both; a `null` side is ignored. */
internal fun union(
    a: Rect?,
    b: Rect?,
): Rect? {
    if (a == null) return b
    if (b == null) return a
    return Rect(minOf(a.left, b.left), minOf(a.top, b.top), maxOf(a.right, b.right), maxOf(a.bottom, b.bottom))
}

/** Window bounds of a placed layout node; empty (zero-area) nodes yield `null` so they never skew a union. */
internal fun boundsOf(layout: LayoutInfo): Rect? {
    return runCatching {
        layout.takeIf { it.isAttached && it.isPlaced }?.coordinates?.boundsInWindow()
    }.getOrNull()?.takeIf { it.width > 0f && it.height > 0f }
}
