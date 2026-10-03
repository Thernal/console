package io.thernal.console.inspector.ui.engine

/** One row of the recomposition ranking. */
internal data class RecompositionEntry(
    val nodeId: Int,
    val name: String,
    val file: String?,
    val count: Int,
    val reason: String?,
)
