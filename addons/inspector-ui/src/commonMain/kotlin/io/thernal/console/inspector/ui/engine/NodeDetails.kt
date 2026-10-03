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

/**
 * `20` when the composable's own recompositions are all there is, `20 own · 40 with framework below` when hidden
 * framework components under it recomposed too.
 */
internal fun NodeDetails.recompositionText(): String {
    if (recompositions == ownRecompositions) return recompositions.toString()
    return "$ownRecompositions own · $recompositions with framework below"
}
