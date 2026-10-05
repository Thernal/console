package io.thernal.console.inspector.ui.engine.geometry

import androidx.compose.ui.geometry.Rect
import kotlin.math.roundToInt

/**
 * One `padding` modifier: the band between the box it adds [outer] and the box it wraps [inner]. [modifierIndex] is
 * where the modifier sits in the chain.
 */
internal data class PaddingRing(
    val outer: Rect,
    val inner: Rect,
    val modifierIndex: Int = 0,
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
    PaddingRing(outer, inner, index).takeIf { it.left != 0f || it.top != 0f || it.right != 0f || it.bottom != 0f }
}
