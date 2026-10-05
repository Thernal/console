@file:OptIn(ComposeToolingApi::class)

package io.thernal.console.inspector.ui.engine.geometry

import androidx.compose.runtime.tooling.ComposeToolingApi
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.LayoutInfo

/**
 * How a node is framed, the way a design tool shows it. [frame] is the box that reads as the component's edge;
 * [margin] is the padding outside it (a `padding` before the background or border) and [padding] the padding inside
 * it, between the frame and the content.
 */
internal data class NodeBox(
    val frame: Rect,
    val margin: NodePadding?,
    val padding: NodePadding?,
)

/**
 * Index of the modifier whose box is the node's frame: the first visual one (background, border...), so paddings
 * before it are margin and paddings after it are inside. With nothing drawn there is no edge to go by, so the frame is
 * the outermost box and every padding counts as inside. `null` when the chain is empty: the frame is the content.
 */
internal fun frameIndex(kinds: List<ModifierKind>): Int? {
    val visual = kinds.indexOf(ModifierKind.Visual)
    return if (visual >= 0) visual else kinds.indices.firstOrNull()
}

/**
 * Splits a node's modifier chain into frame, margin and padding. [chain] holds the box after each modifier, outermost
 * first, and ends with the content box; [kinds] says what each modifier is.
 */
internal fun nodeBox(
    chain: List<Rect?>,
    kinds: List<ModifierKind>,
): NodeBox? {
    val content = chain.lastOrNull() ?: return null
    val frameAt = frameIndex(kinds)
    val frame = frameAt?.let { chain.getOrNull(it) } ?: content
    val rings = paddingRings(chain, kinds.map { it == ModifierKind.Padding })
    val (margin, padding) = rings.partition { frameAt != null && it.modifierIndex < frameAt }
    return NodeBox(
        frame = frame,
        margin = margin.takeIf { it.isNotEmpty() }?.let(::NodePadding),
        padding = padding.takeIf { it.isNotEmpty() }?.let(::NodePadding),
    )
}

/** Reads a node's box from live layout coordinates, so call it per frame while drawing. */
internal fun readNodeBox(layout: LayoutInfo): NodeBox? {
    val content = boundsOf(layout) ?: return null
    val modifiers = runCatching { layout.getModifierInfo() }.getOrDefault(emptyList())
    val chain = modifiers.map { windowBounds(it.coordinates) } + content
    return nodeBox(chain, modifiers.map { ModifierKind.of(it.modifier) })
}

/**
 * Coordinates of the box that frames [layout], or `null` when that is the content box itself. They stay valid while
 * the modifier chain does, so a tree build can find them once and the overlay read them every frame.
 */
internal fun frameCoordinatesOf(layout: LayoutInfo): LayoutCoordinates? {
    val modifiers = runCatching { layout.getModifierInfo() }.getOrNull() ?: return null
    val frameAt = frameIndex(modifiers.map { ModifierKind.of(it.modifier) }) ?: return null
    return modifiers[frameAt].coordinates
}

/** The node's whole box: its content plus everything its own modifiers add around it, margin included. */
internal fun outerBoundsOf(layout: LayoutInfo): Rect? {
    val content = boundsOf(layout) ?: return null
    val first = runCatching { layout.getModifierInfo().firstOrNull() }.getOrNull()?.let { windowBounds(it.coordinates) }
    return union(content, first)
}
