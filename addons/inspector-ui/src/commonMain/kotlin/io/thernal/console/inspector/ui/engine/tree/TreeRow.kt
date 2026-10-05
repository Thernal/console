package io.thernal.console.inspector.ui.engine.tree

/** A visible node together with its display depth. */
internal data class TreeRow(
    val node: InspectorNode,
    val depth: Int,
)
