package io.thernal.console.inspector.ui.overlay

import androidx.compose.ui.geometry.Rect
import io.thernal.console.inspector.InspectorConfig
import io.thernal.console.inspector.ui.engine.tree.InspectorNode
import io.thernal.console.inspector.ui.engine.tree.InspectorSnapshot

/**
 * Everything the draw layer needs for one frame, resolved outside the draw lambda. [marked] are drawn as the
 * selection is (frame, size, padding and margin), for an agent's snapshot of several nodes.
 */
internal class InspectorDrawScene(
    val config: InspectorConfig,
    val snapshot: InspectorSnapshot,
    val nodes: List<InspectorNode>,
    val selected: InspectorNode?,
    val anchor: InspectorNode?,
    val counts: Map<Int, Int>,
    val flashes: Map<Any, Long>,
    val nowMillis: () -> Long,
    val marked: List<InspectorNode> = emptyList(),
) {
    /** Nearest visible ancestor of [node] that has bounds, used as the measurement reference. */
    fun parentBounds(
        node: InspectorNode,
        liveBounds: List<Rect?>,
    ): Rect? {
        var parentId = node.parentId
        while (parentId >= 0) {
            val parent = snapshot.nodes[parentId]
            val isVisible = !(config.hideFrameworkNodes && parent.isFramework)
            if (isVisible) liveBounds[parent.id]?.let { return it }
            parentId = parent.parentId
        }
        return null
    }
}
