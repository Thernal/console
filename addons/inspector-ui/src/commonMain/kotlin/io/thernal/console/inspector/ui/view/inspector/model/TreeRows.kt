package io.thernal.console.inspector.ui.view.inspector.model

import io.thernal.console.inspector.ui.engine.InspectorNode
import io.thernal.console.inspector.ui.engine.InspectorSnapshot

/** Turns a snapshot into the rows of the tree page, honouring collapsed subtrees and the search query. */
internal object TreeRows {
    fun build(
        snapshot: InspectorSnapshot,
        hideFramework: Boolean,
        collapsed: Set<Any>,
        query: String,
        counts: Map<Int, Int>,
        selectedKey: Any?,
    ): List<InspectorRowModel> {
        val rows = snapshot.rows(hideFramework)
        if (query.isNotBlank()) {
            return rows
                .filter { (node, _) -> node.name.contains(query, true) || node.file?.contains(query, true) == true }
                .map { (node, _) -> model(node, 0, false, false, counts, selectedKey) }
        }
        val result = ArrayList<InspectorRowModel>(rows.size)
        var hiddenBelowDepth = Int.MAX_VALUE
        rows.forEachIndexed { index, (node, depth) ->
            if (depth > hiddenBelowDepth) return@forEachIndexed
            hiddenBelowDepth = Int.MAX_VALUE
            val hasChildren = rows.getOrNull(index + 1)?.let { it.depth > depth } == true
            val isCollapsed = hasChildren && node.key in collapsed
            if (isCollapsed) hiddenBelowDepth = depth
            result += model(node, depth, hasChildren, isCollapsed, counts, selectedKey)
        }
        return result
    }

    private fun model(
        node: InspectorNode,
        depth: Int,
        hasChildren: Boolean,
        isCollapsed: Boolean,
        counts: Map<Int, Int>,
        selectedKey: Any?,
    ) = InspectorRowModel(node, depth, hasChildren, isCollapsed, counts[node.id] ?: 0, node.key == selectedKey)
}
