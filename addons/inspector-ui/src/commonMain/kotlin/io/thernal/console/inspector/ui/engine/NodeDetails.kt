package io.thernal.console.inspector.ui.engine

/** Everything the details panel shows about one node, computed once per selection. */
internal data class NodeDetails(
    val name: String,
    val definedIn: String?,
    val parentName: String?,
    val boundsPx: String?,
    val sizePx: String?,
    val sizeDp: String?,
    val positionDp: String?,
    val padding: String?,
    val density: String?,
    val layoutDirection: String?,
    val recompositions: Int,
    val ownRecompositions: Int,
    val skippedPasses: Int?,
    val lastReason: String?,
    val modifiers: List<String>,
    val parameterNames: List<String>,
    val slotValues: List<String>,
)
