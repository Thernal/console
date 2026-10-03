package io.thernal.console.inspector.ui.engine

import androidx.compose.ui.geometry.Rect

/** Smallest rect containing both; a `null` side is ignored. */
internal fun union(
    a: Rect?,
    b: Rect?,
): Rect? {
    if (a == null) return b
    if (b == null) return a
    return Rect(minOf(a.left, b.left), minOf(a.top, b.top), maxOf(a.right, b.right), maxOf(a.bottom, b.bottom))
}
