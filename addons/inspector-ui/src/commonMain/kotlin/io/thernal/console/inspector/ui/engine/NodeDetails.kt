package io.thernal.console.inspector.ui.engine

/**
 * Everything the details panel shows about one node, computed once per selection. [anchorName] is the measuring
 * anchor when one is set; [distanceFromAnchor] the distance from it, or null when this node is the anchor.
 */
internal data class NodeDetails(
    val name: String,
    val definedIn: String?,
    val parentName: String?,
    val boundsPx: String?,
    val sizePx: String?,
    val sizeDp: String?,
    val positionDp: String?,
    val padding: String?,
    val margin: String?,
    val density: String?,
    val layoutDirection: String?,
    val recompositions: Int,
    val ownRecompositions: Int,
    val skippedPasses: Int?,
    val lastReason: String?,
    val modifiers: List<String>,
    val parameterNames: List<String>,
    val slotValues: List<String>,
    val isAnchor: Boolean = false,
    val anchorName: String? = null,
    val distanceFromAnchor: String? = null,
)
