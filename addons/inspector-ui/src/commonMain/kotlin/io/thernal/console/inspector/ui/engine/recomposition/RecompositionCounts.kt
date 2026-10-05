package io.thernal.console.inspector.ui.engine.recomposition

import androidx.compose.runtime.RecomposeScope
import io.thernal.console.inspector.ui.engine.tree.InspectorNode
import io.thernal.console.inspector.ui.engine.tree.InspectorSnapshot

/**
 * Recomposition counts per scope, plus which scopes have been seen at all (a scope's first pass is its initial
 * composition, not a recomposition). Keys are group identities or, for scopes without one, the scope object itself.
 *
 * Scope objects are only held while their group is in the tree. When one disappears its count moves to the node that
 * owned it, so history survives without keeping dead compositions alive.
 */
internal class RecompositionCounts {
    private val counts = HashMap<Any, Int>()
    private val skips = HashMap<Any, Int>()
    private val seen = HashSet<Any>()
    private val retired = HashMap<Any, Int>()
    private val retiredSkips = HashMap<Any, Int>()

    /** Marks [key] as seen. True the first time, which is the scope's initial composition. */
    fun isFirstSight(key: Any): Boolean {
        return seen.add(key)
    }

    fun increment(key: Any) {
        counts[key] = (counts[key] ?: 0) + 1
    }

    /** A pass that did not run the function: the scope was re-entered but its parameters had not changed. */
    fun skip(key: Any) {
        skips[key] = (skips[key] ?: 0) + 1
    }

    fun markSeen(keys: Collection<Any>) {
        seen.addAll(keys)
    }

    fun reset() {
        counts.clear()
        skips.clear()
        retired.clear()
        retiredSkips.clear()
    }

    /** Moves the counts of scopes that left the tree, as of [old], onto their owning nodes. */
    fun retireMissing(
        old: InspectorSnapshot,
        new: InspectorSnapshot,
    ) {
        val gone = seen.filter { it is RecomposeScope && !new.knowsIdentity(it) }
        gone.forEach { key ->
            val owner = old.ownerOf(key) ?: return@forEach
            counts.remove(key)?.takeIf { it > 0 }?.let { retired[owner.key] = (retired[owner.key] ?: 0) + it }
            skips.remove(key)?.takeIf { it > 0 }?.let { retiredSkips[owner.key] = (retiredSkips[owner.key] ?: 0) + it }
        }
        seen.removeAll(gone.toSet())
    }

    /**
     * Counts per node id of [snapshot], with hidden framework nodes passing theirs up to the app when asked. With
     * [skipped] set it returns the skipped passes instead of the recompositions that ran.
     */
    fun byNode(
        snapshot: InspectorSnapshot,
        hideFramework: Boolean,
        skipped: Boolean = false,
    ): Map<Int, Int> {
        val byNode = HashMap<Int, Int>()
        fun credit(
            owner: InspectorNode?,
            count: Int,
        ) {
            snapshot.creditedNode(owner, hideFramework)?.let { byNode[it.id] = (byNode[it.id] ?: 0) + count }
        }
        (if (skipped) skips else counts).forEach { (key, count) -> credit(snapshot.ownerOf(key), count) }
        (if (skipped) retiredSkips else retired).forEach { (nodeKey, count) ->
            credit(snapshot.nodeByKey(nodeKey), count)
        }
        return byNode
    }
}
