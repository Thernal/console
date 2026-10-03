package io.thernal.console.inspector.ui.engine

/** Read-only views over the engine's current state. */
internal object InspectorQueries {
    fun selectedNode(): InspectorNode? {
        return InspectorEngine.snapshot.value.nodeByKey(InspectorEngine.selectedKey.value)
    }

    fun detailsOf(node: InspectorNode): NodeDetails {
        val snapshot = InspectorEngine.snapshot.value
        return NodeDetailsReader.read(
            snapshot = snapshot,
            node = node,
            bounds = snapshot.liveBounds()[node.id],
            recompositions = InspectorEngine.nodeCounts.value[node.id] ?: 0,
            ownRecompositions = InspectorEngine.ownNodeCounts.value[node.id] ?: 0,
            skippedPasses = if (canReadSkips) InspectorEngine.skippedCounts.value[node.id] ?: 0 else null,
            lastReason = InspectorEngine.reasonOf(node.key),
        )
    }

    fun ranking(
        limit: Int,
        hideFramework: Boolean,
    ): List<RecompositionEntry> {
        val snapshot = InspectorEngine.snapshot.value
        return InspectorEngine.nodeCounts.value.entries
            .mapNotNull { (id, count) -> snapshot.nodes.getOrNull(id)?.let { it to count } }
            .filter { (node, _) -> !(hideFramework && node.isFramework) }
            .sortedByDescending { it.second }
            .take(limit)
            .map { (node, count) ->
                RecompositionEntry(node.id, node.name, node.file, count, InspectorEngine.reasonOf(node.key))
            }
    }
}
