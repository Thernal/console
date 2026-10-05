package io.thernal.console.inspector.ui.view.inspector

import io.thernal.console.inspector.ui.engine.tree.InspectorSnapshot

/** Plain-text export of the visible tree, for sharing in bug reports. */
internal object TreeExporter {
    fun export(
        snapshot: InspectorSnapshot,
        hideFramework: Boolean,
        counts: Map<Int, Int>,
    ): String = buildString {
        val bounds = snapshot.liveBounds()
        snapshot.rows(hideFramework).forEach { (node, depth) ->
            append("  ".repeat(depth))
            append(node.name)
            node.file?.let { append(" (").append(it).append(')') }
            bounds[node.id]?.let { append(" [${it.width.toInt()}x${it.height.toInt()}px]") }
            counts[node.id]?.takeIf { it > 0 }?.let { append(" ×").append(it) }
            append('\n')
        }
    }
}
