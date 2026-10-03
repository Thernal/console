@file:OptIn(ComposeToolingApi::class)

package io.thernal.console.inspector.ui.engine

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.LayoutInfo
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.platform.InspectableValue
import androidx.compose.runtime.tooling.ComposeToolingApi
import kotlin.math.roundToInt

private const val PADDING_MODIFIER = "padding"

/** One `padding` modifier: the band between the box it adds [outer] and the box it wraps [inner]. */
internal data class PaddingRing(
    val outer: Rect,
    val inner: Rect,
) {
    val left: Float get() = inner.left - outer.left
    val top: Float get() = inner.top - outer.top
    val right: Float get() = outer.right - inner.right
    val bottom: Float get() = outer.bottom - inner.bottom
}

/**
 * The padding a node adds around its own content, read from where each `padding` modifier sits in the chain.
 * [rings] go from the outermost modifier inwards.
 */
internal data class NodePadding(
    val rings: List<PaddingRing>,
) {
    val left: Float get() = rings.sumOf { it.left.toDouble() }.toFloat()
    val top: Float get() = rings.sumOf { it.top.toDouble() }.toFloat()
    val right: Float get() = rings.sumOf { it.right.toDouble() }.toFloat()
    val bottom: Float get() = rings.sumOf { it.bottom.toDouble() }.toFloat()

    /** `top 12 · left 16 · right 16 · bottom 12 dp`, leaving out sides without padding. */
    fun describe(density: Float): String? {
        fun side(
            name: String,
            px: Float,
        ) = (px / density).roundToInt().takeIf { it != 0 }?.let { "$name $it" }
        val parts = listOfNotNull(side("top", top), side("left", left), side("right", right), side("bottom", bottom))
        return if (parts.isEmpty()) null else parts.joinToString(" · ") + " dp"
    }
}

/**
 * Pairs every padding modifier with the box inside it. [chain] holds the box after each modifier, outermost first,
 * and ends with the node's own content box; [isPadding] says which of the modifiers are paddings.
 */
internal fun paddingRings(
    chain: List<Rect?>,
    isPadding: List<Boolean>,
): List<PaddingRing> = isPadding.indices.mapNotNull { index ->
    if (!isPadding[index]) return@mapNotNull null
    val outer = chain.getOrNull(index) ?: return@mapNotNull null
    val inner = chain.drop(index + 1).firstOrNull { it != null } ?: return@mapNotNull null
    PaddingRing(outer, inner).takeIf { it.left != 0f || it.top != 0f || it.right != 0f || it.bottom != 0f }
}

/** Reads a node's padding from live layout coordinates, so call it per frame while drawing. */
internal fun readNodePadding(layout: LayoutInfo): NodePadding? {
    val content = boundsOf(layout) ?: return null
    val modifiers = runCatching { layout.getModifierInfo() }.getOrDefault(emptyList())
    if (modifiers.isEmpty()) return null
    val chain = modifiers.map { windowBounds(it.coordinates) } + content
    val isPadding = modifiers.map { (it.modifier as? InspectableValue)?.nameFallback == PADDING_MODIFIER }
    return paddingRings(chain, isPadding).takeIf { it.isNotEmpty() }?.let(::NodePadding)
}

private fun windowBounds(coordinates: LayoutCoordinates): Rect? =
    runCatching { coordinates.takeIf { it.isAttached }?.boundsInWindow() }.getOrNull()

/** The node's whole box: its content plus everything its own modifiers add around it, padding included. */
internal fun outerBoundsOf(layout: LayoutInfo): Rect? {
    val content = boundsOf(layout) ?: return null
    val first = runCatching { layout.getModifierInfo().firstOrNull() }.getOrNull()?.let { windowBounds(it.coordinates) }
    return union(content, first)
}
