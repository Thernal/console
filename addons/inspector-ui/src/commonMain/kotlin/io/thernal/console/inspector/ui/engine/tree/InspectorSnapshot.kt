package io.thernal.console.inspector.ui.engine.tree

import androidx.compose.ui.geometry.Rect
import io.thernal.console.inspector.ui.engine.geometry.boundsOf
import io.thernal.console.inspector.ui.engine.geometry.union

/** Immutable result of one tree build. */
internal class InspectorSnapshot(
    val nodes: List<InspectorNode>,
    private val idByKey: Map<Any, Int>,
    private val idByGroupIdentity: Map<Any?, Int>,
    val buildMicros: Long,
    val subCompositionCount: Int,
) {
    fun nodeByKey(key: Any?): InspectorNode? {
        return idByKey[key]?.let { nodes.getOrNull(it) }
    }

    /** Nearest named composable that owns the group with this identity, e.g. a recompose scope. */
    fun ownerOf(identity: Any?): InspectorNode? {
        return idByGroupIdentity[identity]?.let { nodes.getOrNull(it) }
    }

    /** Whether [node] is, or sits inside, one of the inspector's own composables. */
    fun isInspectorOwn(node: InspectorNode): Boolean {
        var current: InspectorNode? = node
        while (current != null) {
            if (isInspectorOwnFile(current.file)) return true
            current = nodes.getOrNull(current.parentId)
        }
        return false
    }

    fun knowsIdentity(identity: Any?): Boolean {
        return idByGroupIdentity.containsKey(identity)
    }

    fun visibleNodes(hideFramework: Boolean): List<InspectorNode> =
        if (hideFramework) nodes.filterNot { it.isFramework } else nodes

    /**
     * Current window bounds of every node, indexed by node id: the union of its own layout nodes and
     * its children's bounds. Read from live layout coordinates, so call it per frame while drawing.
     */
    fun liveBounds(): List<Rect?> {
        val result = arrayOfNulls<Rect>(nodes.size)
        for (index in nodes.indices.reversed()) {
            val node = nodes[index]
            var bounds = result[index]
            node.layouts.forEach { bounds = union(bounds, boundsOf(it)) }
            result[index] = bounds
            if (node.parentId >= 0) result[node.parentId] = union(result[node.parentId], bounds)
        }
        return result.asList()
    }

    /** The node a recomposition of [owner] is credited to: with [hideFramework], hidden framework nodes pass it up. */
    fun creditedNode(
        owner: InspectorNode?,
        hideFramework: Boolean,
    ): InspectorNode? {
        var node = owner
        if (!hideFramework) return node
        while (node != null && node.isFramework) node = nodes.getOrNull(node.parentId)
        return node
    }

    fun identities(): Set<Any> {
        return idByGroupIdentity.keys.filterNotNull().toSet()
    }

    /** Visible nodes in tree order with the indentation depth among visible ancestors only. */
    fun rows(hideFramework: Boolean): List<TreeRow> {
        val depths = IntArray(nodes.size)
        val visible = ArrayList<TreeRow>(nodes.size)
        nodes.forEach { node ->
            val parentDepth = if (node.parentId >= 0) depths[node.parentId] else -1
            val isShown = !(hideFramework && node.isFramework)
            depths[node.id] = if (isShown) parentDepth + 1 else parentDepth
            if (isShown) visible += TreeRow(node, depths[node.id])
        }
        return visible
    }

    companion object {
        val Empty = InspectorSnapshot(emptyList(), emptyMap(), emptyMap(), 0L, 0)
    }
}
