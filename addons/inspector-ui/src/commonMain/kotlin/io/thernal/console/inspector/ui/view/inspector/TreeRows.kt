package io.thernal.console.inspector.ui.view.inspector

import io.thernal.console.inspector.ui.engine.tree.InspectorNode
import io.thernal.console.inspector.ui.engine.tree.InspectorSnapshot
import io.thernal.console.inspector.ui.view.inspector.model.InspectorRowModel

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
                .filter { (node, _) ->
                    node.name.contains(query, ignoreCase = true) ||
                        node.file?.contains(query, ignoreCase = true) == true
                }
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
    ) = InspectorRowModel(
        id = node.id,
        key = node.key,
        name = node.name,
        file = node.file,
        isFramework = node.isFramework,
        depth = depth,
        hasChildren = hasChildren,
        isCollapsed = isCollapsed,
        count = counts[node.id] ?: 0,
        isSelected = node.key == selectedKey,
    )
}
