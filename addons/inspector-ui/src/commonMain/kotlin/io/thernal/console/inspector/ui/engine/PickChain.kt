package io.thernal.console.inspector.ui.engine

import androidx.compose.ui.geometry.Rect
import kotlin.math.abs

/**
 * Design-tool style picking: a tap selects the outermost component under it, and tapping inside the selection again
 * steps one level in. The chain is every candidate under the point, outermost first.
 */
internal object PickChain {
    /**
     * Collapses each run of nested candidates with the same bounds to its outermost one (a composable and the layout
     * it wraps read as one item) and drops the outermost run when something is nested inside it, so a screen-filling
     * root is never the first pick.
     */
    fun <T> collapse(
        chain: List<T>,
        boundsOf: (T) -> Rect?,
    ): List<T> {
        val runs = ArrayList<T>(chain.size)
        var lastBounds: Rect? = null
        chain.forEach { item ->
            val bounds = boundsOf(item)
            if (runs.isEmpty() || !sameBounds(bounds, lastBounds)) runs += item
            lastBounds = bounds
        }
        return if (runs.size > 1) runs.drop(1) else runs
    }

    /**
     * The pick for a fresh tap given what is selected now. On the chain, the selection steps one level in (and stays
     * put at the innermost); off it, the pick is the outermost candidate that does not hold the selection, i.e. on its
     * level or beside it; with nothing selected, the outermost candidate.
     */
    fun <T> stepInto(
        chain: List<T>,
        current: T?,
        holdsCurrent: (T) -> Boolean,
    ): T? {
        if (chain.isEmpty()) return null
        if (current == null) return chain.first()
        val index = chain.indexOf(current)
        if (index >= 0) return chain[minOf(index + 1, chain.lastIndex)]
        return chain.firstOrNull { !holdsCurrent(it) } ?: chain.last()
    }

    private fun sameBounds(
        a: Rect?,
        b: Rect?,
    ): Boolean {
        if (a == null || b == null) return a == b
        return abs(a.left - b.left) < SAME_EDGE_PX && abs(a.top - b.top) < SAME_EDGE_PX &&
            abs(a.right - b.right) < SAME_EDGE_PX && abs(a.bottom - b.bottom) < SAME_EDGE_PX
    }
}

/** Edges closer than this, in px, count as the same edge. */
private const val SAME_EDGE_PX = 0.5f
