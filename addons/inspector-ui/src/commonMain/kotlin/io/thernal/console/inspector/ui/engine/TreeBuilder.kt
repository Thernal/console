@file:OptIn(ComposeToolingApi::class)

package io.thernal.console.inspector.ui.engine

import androidx.compose.runtime.RecomposeScope
import androidx.compose.runtime.tooling.ComposeToolingApi
import androidx.compose.runtime.tooling.CompositionData
import androidx.compose.runtime.tooling.CompositionGroup
import androidx.compose.runtime.tooling.SourceInformation
import androidx.compose.runtime.tooling.parseSourceInformation
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutInfo
import androidx.compose.ui.layout.boundsInWindow
import kotlin.time.TimeSource

private class PendingNode(
    val key: Any,
    val id: Int,
    val parentId: Int,
    val depth: Int,
    val name: String,
    val file: String?,
    val isUnderApp: Boolean,
    val group: CompositionGroup,
) {
    val layouts = mutableListOf<LayoutInfo>()
    val childIds = mutableListOf<Int>()
    var firstLayout: LayoutInfo? = null
}

private class Frame(
    val group: CompositionGroup,
    val parent: PendingNode?,
    val isRoot: Boolean,
)

/** The wrapper that surrounds the app content; everything above it belongs to Console, not the app. */
private const val HOST_NAME = "InspectorHost"

/** Reads slot tables into an [InspectorSnapshot]. Must run on the thread that owns the composition. */
internal object TreeBuilder {
    private const val MAX_NODES = 20_000

    fun build(
        tables: List<CompositionData>,
        appTags: Set<String>,
    ): InspectorSnapshot {
        val mark = TimeSource.Monotonic.markNow()
        val nodes = mutableListOf<PendingNode>()
        val idByGroupIdentity = HashMap<Any?, Int>()
        val infoCache = HashMap<String, SourceInformation?>()

        tables.forEachIndexed { index, data ->
            val stack = ArrayDeque<Frame>()
            data.compositionGroups.reversed().forEach { stack.addLast(Frame(it, null, isRoot = index == 0)) }
            while (stack.isNotEmpty() && nodes.size < MAX_NODES) {
                val frame = stack.removeLast()
                val owner = visit(frame, nodes, idByGroupIdentity, infoCache)
                frame.group.compositionGroups.reversed().forEach { stack.addLast(Frame(it, owner, frame.isRoot)) }
            }
        }

        val built = nodes.map { toNode(it, appTags) }
        return InspectorSnapshot(
            nodes = built,
            idByKey = built.associate { it.key to it.id },
            idByGroupIdentity = idByGroupIdentity,
            buildMicros = mark.elapsedNow().inWholeMicroseconds,
            subCompositionCount = (tables.size - 1).coerceAtLeast(0),
        )
    }

    private fun visit(
        frame: Frame,
        nodes: MutableList<PendingNode>,
        idByGroupIdentity: MutableMap<Any?, Int>,
        infoCache: MutableMap<String, SourceInformation?>,
    ): PendingNode? {
        val group = frame.group
        val info = group.sourceInfo?.let { raw -> infoCache.getOrPut(raw) { parseSourceInformation(raw) } }
        val name = info?.takeIf { it.isCall }?.functionName?.takeIf { !isGenericName(it) }
        var owner = frame.parent
        if (name != null) {
            val created = PendingNode(
                key = group.identity ?: group,
                id = nodes.size,
                parentId = frame.parent?.id ?: -1,
                depth = (frame.parent?.depth ?: -1) + 1,
                name = name,
                file = info.sourceFile,
                isUnderApp = !frame.isRoot || frame.parent?.isUnderApp == true || frame.parent?.name == HOST_NAME,
                group = group,
            )
            frame.parent?.childIds?.add(created.id)
            nodes += created
            owner = created
        }
        owner?.let { named ->
            idByGroupIdentity[group.identity] = named.id
            // Many scopes never get an identity, but the group's own slots hold the scope object.
            group.data.forEach { item -> if (item is RecomposeScope) idByGroupIdentity[item] = named.id }
        }
        (group.node as? LayoutInfo)?.let { layout -> attachLayout(owner, layout, nodes) }
        return owner
    }

    private fun attachLayout(
        owner: PendingNode?,
        layout: LayoutInfo,
        nodes: List<PendingNode>,
    ) {
        owner ?: return
        owner.layouts += layout
        var current: PendingNode? = owner
        while (current != null && current.firstLayout == null) {
            current.firstLayout = layout
            current = nodes.getOrNull(current.parentId)
        }
    }

    private fun toNode(
        pending: PendingNode,
        appTags: Set<String>,
    ): InspectorNode = InspectorNode(
        key = pending.key,
        id = pending.id,
        parentId = pending.parentId,
        depth = pending.depth,
        name = pending.name,
        file = pending.file,
        isFramework = !pending.isUnderApp || isFrameworkNode(pending.name, pending.file, appTags),
        isUnderApp = pending.isUnderApp,
        layouts = pending.layouts,
        primaryLayout = pending.firstLayout,
        childIds = pending.childIds,
        group = pending.group,
    )
}

/** Window bounds of a placed layout node; empty (zero-area) nodes yield `null` so they never skew a union. */
internal fun boundsOf(layout: LayoutInfo): Rect? {
    return runCatching {
        layout.takeIf { it.isAttached && it.isPlaced }?.coordinates?.boundsInWindow()
    }.getOrNull()?.takeIf { it.width > 0f && it.height > 0f }
}
