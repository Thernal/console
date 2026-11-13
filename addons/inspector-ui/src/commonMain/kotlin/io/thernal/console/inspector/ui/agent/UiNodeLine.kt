package io.thernal.console.inspector.ui.agent

/**
 * One node as the agent tools print it. [bounds] is `x,y w×h` in dp, or null when the node has no layout on screen;
 * [texts] are the strings it and its hidden framework children hold, such as a `Text`'s text.
 */
internal data class UiNodeLine(
    val id: Int,
    val depth: Int,
    val name: String,
    val file: String?,
    val bounds: String?,
    val recompositions: Int,
    val texts: List<String>,
)
