package io.thernal.console.inspector.ui.engine

import androidx.compose.ui.geometry.Offset
import io.thernal.console.inspector.ConsoleInspector
import io.thernal.console.inspector.ui.engine.geometry.outerBoundsOf
import io.thernal.console.inspector.ui.engine.geometry.pickRect
import io.thernal.console.inspector.ui.engine.tree.InspectorNode
import io.thernal.console.inspector.ui.engine.tree.InspectorSnapshot

/** Turns touches on the app into the engine's selection and the measuring anchor. Runs on the main thread. */
internal object InspectorPicker {
    /**
     * Picks at [point] (window coordinates). A press steps through the components under it the way a design tool
     * does (see [PickChain]); a drag ([isPress] false) picks the smallest component under the finger.
     */
    fun pick(
        point: Offset,
        isPress: Boolean = true,
    ) {
        InspectorEngine.refresh(force = true)
        val snapshot = InspectorEngine.snapshot.value
        val bounds = snapshot.liveBounds()
        // Picking reaches every component, Text and Icon included: the framework filter only thins out the tree.
        val hits = snapshot.visibleNodes(hideFramework = false).mapNotNull { node ->
            val frame = bounds[node.id] ?: return@mapNotNull null
            val area = pickRect(frame, point) { node.primaryLayout?.let(::outerBoundsOf) }
            if (area.contains(point)) node to area else null
        }
        val deepest = hits
            .minWithOrNull(compareBy({ it.second.width * it.second.height }, { -it.first.depth }))
            ?.first
        val hitIds = hits.mapTo(HashSet()) { it.first.id }
        val chain = PickChain.collapse(pickChain(snapshot, deepest, hitIds)) { bounds[it.id] }
        when {
            InspectorMeasure.isMeasuring.value -> pickWhileMeasuring(snapshot, chain, deepest, isPress)

            isPress -> {
                val current = snapshot.nodeByKey(InspectorEngine.selectedKey.value)
                val next = PickChain.stepInto(chain, current) { current != null && snapshot.holds(it, current) }
                InspectorEngine.select(next?.key)
            }

            else -> InspectorEngine.select(deepest?.key)
        }
    }

    /**
     * The app components under a pick, outermost first: [deepest] and those of its ancestors the pick hit, leaving out
     * Console's own UI and, with the framework filter on, hidden framework wrappers other than [deepest] itself.
     */
    private fun pickChain(
        snapshot: InspectorSnapshot,
        deepest: InspectorNode?,
        hitIds: Set<Int>,
    ): List<InspectorNode> {
        val hideFramework = ConsoleInspector.config.value.hideFrameworkNodes
        val chain = ArrayList<InspectorNode>()
        var node = deepest
        while (node != null) {
            val isShown = node === deepest || !(hideFramework && node.isFramework)
            if (node.id in hitIds && node.isUnderApp && isShown) chain += node
            node = snapshot.nodes.getOrNull(node.parentId)
        }
        return chain.reversed()
    }

    /**
     * Picking while measuring. The first tap sets the anchor; taps inside the anchor step it one level in, taps
     * elsewhere pick the second item on the anchor's level and taps inside that item step it in. A drag picks the
     * smallest item under the finger as the second one.
     */
    private fun pickWhileMeasuring(
        snapshot: InspectorSnapshot,
        chain: List<InspectorNode>,
        deepest: InspectorNode?,
        isPress: Boolean,
    ) {
        val anchor = snapshot.nodeByKey(InspectorMeasure.anchorKey.value)
        val target = snapshot.nodeByKey(InspectorEngine.selectedKey.value)?.takeIf { it !== anchor }
        when {
            !isPress -> if (anchor != null && deepest != null && deepest !== anchor) InspectorEngine.select(deepest.key)

            anchor == null -> {
                val first = PickChain.stepInto(chain, null) { false }
                InspectorMeasure.setAnchor(first?.key)
                InspectorEngine.select(first?.key)
            }

            target != null && target in chain -> {
                InspectorEngine.select(PickChain.stepInto(chain, target) { snapshot.holds(it, target) }?.key)
            }

            anchor in chain -> {
                val next = PickChain.stepInto(chain, anchor) { snapshot.holds(it, anchor) }
                InspectorMeasure.setAnchor(next?.key)
                if (target == null) InspectorEngine.select(next?.key)
            }

            else -> InspectorEngine.select(PickChain.stepInto(chain, anchor) { snapshot.holds(it, anchor) }?.key)
        }
    }
}
