package io.thernal.console.inspector.ui.agent

import io.thernal.console.inspector.ui.engine.tree.InspectorNode
import io.thernal.console.inspector.ui.engine.tree.InspectorSnapshot

/**
 * Short ids for the agent tools, kept per node key, so a node keeps its id across tree rebuilds while its composable
 * stays on screen (the snapshot's own ids are positions and shift whenever the tree changes). Main thread only.
 */
internal object InspectorAgentIds {
    private val idByKey = HashMap<Any, Int>()
    private val keyById = HashMap<Int, Any>()
    private var nextId = 1

    fun idOf(node: InspectorNode): Int {
        return idByKey.getOrPut(node.key) {
            val id = nextId++
            keyById[id] = node.key
            id
        }
    }

    fun nodeOf(
        id: Int,
        snapshot: InspectorSnapshot,
    ): InspectorNode? {
        return keyById[id]?.let(snapshot::nodeByKey)
    }

    /** Forgets the ids of nodes that left the screen, so the table only holds what [snapshot] shows. */
    fun retain(snapshot: InspectorSnapshot) {
        val keys = snapshot.nodes.mapTo(HashSet()) { it.key }
        idByKey.keys.retainAll(keys)
        keyById.values.retainAll(keys)
    }
}
