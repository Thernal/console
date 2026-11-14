package io.thernal.console.inspector.ui.agent

import androidx.compose.ui.unit.IntSize
import io.thernal.console.inspector.ConsoleInspector
import io.thernal.console.inspector.InspectorConfig
import io.thernal.console.inspector.ui.engine.InspectorEngine
import io.thernal.console.inspector.ui.engine.InspectorQueries
import io.thernal.console.inspector.ui.engine.tree.InspectorNode
import io.thernal.console.inspector.ui.engine.tree.InspectorSnapshot
import io.thernal.console.inspector.ui.host.CapturedFrame
import io.thernal.console.inspector.ui.host.ContentCapture
import io.thernal.console.inspector.ui.overlay.InspectorDrawScene
import io.thernal.console.inspector.ui.overlay.drawInspectorScene
import io.thernal.console.inspector.ui.view.inspector.toText
import kotlin.math.max
import kotlin.math.min

private const val NO_UI = "No UI on screen to inspect yet."
private const val MAX_CHILDREN = 30
private const val TURN_ON = "settings_set(section=\"inspector\", key=\"enabled\", value=\"true\")"
private const val FRAME_TIMEOUT_MILLIS = 3_000L
private const val SNAPSHOT_LONG_SIDE_PX = 1_600f
private const val NO_FRAME = "The app drew no frame within $FRAME_TIMEOUT_MILLIS ms (is it in the background, or the " +
    "screen off?), so there is no image."
private const val NO_HOST = "The app content is not on screen, so there is no image."

/** Main thread only: each read rebuilds the tree first, so it shows the screen as it is now. */
internal fun uiTree(
    query: String?,
    depth: Int?,
    framework: Boolean,
    limit: Int,
): String {
    val snapshot = refreshed() ?: return NO_UI
    val config = ConsoleInspector.config.value
    val hideFramework = config.hideFrameworkNodes && !framework
    val reader = UiTreeReader(snapshot, hideFramework)
    val lines = selectRows(reader.rows(), query, depth)
    if (lines.isEmpty()) return "No composable named or in a file like \"$query\"; `ui_find` searches texts too."
    val hidden = if (hideFramework) ", framework nodes hidden (`framework = true` shows them)" else ""
    val header = "${lines.size} of ${snapshot.nodes.size} nodes$hidden. Bounds: x,y w×h in dp, " +
        "${reader.density} px per dp; ×N: recompositions since the last reset.${offHint(config)}"
    return treeText(header, lines, limit)
}

internal fun uiNode(id: Int): String {
    val snapshot = refreshed() ?: return NO_UI
    val node = InspectorAgentIds.nodeOf(id, snapshot)
        ?: error("No node #$id on screen now; `ui_tree` or `ui_find` gives the current ids")
    val reader = UiTreeReader(snapshot, ConsoleInspector.config.value.hideFrameworkNodes)
    val lines = mutableListOf("#$id " + InspectorQueries.detailsOf(node).toText().trimEnd())
    val texts = reader.texts(node)
    if (texts.isNotEmpty()) lines += "Texts: " + texts.joinToString { "\"$it\"" }
    reader.testTags(node).takeIf { it.isNotEmpty() }?.let { lines += "Test tags: " + it.joinToString() }
    reader.insideText(node).takeIf { it.isNotEmpty() }?.let { lines += "Inside:" + it.removePrefix(" in") }
    val children = reader.children(node)
    if (children.isNotEmpty()) {
        val shown = children.take(MAX_CHILDREN).joinToString { "#${InspectorAgentIds.idOf(it)} ${it.name}" }
        val more = if (children.size > MAX_CHILDREN) " … ${children.size - MAX_CHILDREN} more" else ""
        lines += "Children: $shown$more"
    }
    return lines.joinToString(separator = "\n")
}

internal fun uiFind(
    query: String,
    limit: Int,
): String {
    require(query.isNotBlank()) { "Pass a non-empty text" }
    val snapshot = refreshed() ?: return NO_UI
    val config = ConsoleInspector.config.value
    val reader = UiTreeReader(snapshot, config.hideFrameworkNodes)
    val matches = snapshot.nodes.filter { node ->
        node.name.contains(query, ignoreCase = true) ||
            node.file?.contains(query, ignoreCase = true) == true ||
            reader.texts(node, ownOnly = true).any { it.contains(query, ignoreCase = true) } ||
            reader.testTags(node).any { it.contains(query, ignoreCase = true) }
    }
    if (matches.isEmpty()) return "No node's name, file, text or test tag contains \"$query\".${offHint(config)}"
    val lines = matches.take(limit).map { node ->
        uiLine(reader.line(node, ownTextsOnly = true), indent = false) + reader.insideText(node)
    }
    val more = if (matches.size > limit) listOf("… ${matches.size - limit} more") else emptyList()
    return (listOf("${matches.size} nodes match \"$query\":") + lines + more).joinToString(separator = "\n")
}

internal fun recompositionsText(limit: Int): String {
    val config = ConsoleInspector.config.value
    if (!config.enabled || !config.trackRecompositions) {
        return "Recompositions are not tracked: turn on `enabled` and `trackRecompositions` in the settings " +
            "section \"inspector\"."
    }
    val snapshot = refreshed() ?: return NO_UI
    val reader = UiTreeReader(snapshot, config.hideFrameworkNodes)
    val ranking = InspectorQueries.ranking(limit, config.hideFrameworkNodes)
    val timing = InspectorEngine.timing.value
    val header = "Since the last reset: ${timing.count} composition passes ran app code, average " +
        "${timing.averageMicros} µs, longest ${timing.maxMicros} µs."
    if (ranking.isEmpty()) return "$header\nNothing has recomposed."
    val lines = ranking.mapNotNull { entry ->
        val node = snapshot.nodes.getOrNull(entry.nodeId) ?: return@mapNotNull null
        val reason = entry.reason?.let { " — $it" }.orEmpty()
        uiLine(reader.line(node), indent = false) + reason
    }
    return (listOf(header) + lines).joinToString(separator = "\n")
}

/**
 * Main thread: the app's next frame scaled by [scale] (by default to a long side of about 1600 px), with the nodes
 * [ids] marked as the selection is, and the heatmap and ×N counters when asked. Only nodes in the image's window are
 * drawn; the others are named in the text with the reason.
 */
internal suspend fun drawUiSnapshot(
    ids: List<Int>,
    hasHeatmap: Boolean,
    hasCounters: Boolean,
    scale: Float?,
): UiSnapshotImage {
    val isAttached = ContentCapture.isAttached
    val frame = ContentCapture.nextFrame(FRAME_TIMEOUT_MILLIS)
        ?: return UiSnapshotImage(image = null, text = if (isAttached) NO_FRAME else NO_HOST, scale = 1f)
    InspectorEngine.refresh(force = true)
    val snapshot = InspectorEngine.snapshot.value
    InspectorAgentIds.retain(snapshot)
    val config = ConsoleInspector.config.value
    val notes = mutableListOf<String>()
    val marked = markedNodes(ids = ids, snapshot = snapshot, frame = frame, notes = notes)
    val isCounted = hasHeatmap || hasCounters
    if (isCounted && (!config.enabled || !config.trackRecompositions)) {
        notes += "Recompositions are not tracked (`enabled` and `trackRecompositions` in the settings section " +
            "\"inspector\"), so the heatmap and counters show nothing."
    }
    offHint(config).takeIf { it.isNotEmpty() }?.let { notes += it.trim() }
    val size = frame.size
    val drawScale = scale ?: min(1f, SNAPSHOT_LONG_SIDE_PX / max(size.width, size.height).coerceAtLeast(1))
    val scene = InspectorDrawScene(
        config = config.copy(
            showBounds = false,
            showLabels = false,
            showHeatmap = hasHeatmap,
            showRecompositionCounters = hasCounters,
            showMeasurements = true,
        ),
        snapshot = snapshot,
        nodes = if (isCounted) snapshot.visibleNodes(config.hideFrameworkNodes).filter(frame::shows) else emptyList(),
        selected = null,
        anchor = null,
        counts = InspectorEngine.nodeCounts.value,
        flashes = emptyMap(),
        nowMillis = InspectorEngine::nowMillis,
        marked = marked,
    )
    val image = frame.render(drawScale) { drawInspectorScene(scene, frame.style, frame.measurer) }
    val reader = UiTreeReader(snapshot, config.hideFrameworkNodes)
    val text = snapshotText(
        screen = size,
        density = frame.density.density,
        image = IntSize(image.width, image.height),
        scale = drawScale,
        marked = marked.map { reader.line(it, ownTextsOnly = true) },
        notes = notes,
    )
    return UiSnapshotImage(image = image, text = text, scale = drawScale)
}

/** The nodes of [ids] the image can show; for each one it cannot, a line in [notes] says why. */
private fun markedNodes(
    ids: List<Int>,
    snapshot: InspectorSnapshot,
    frame: CapturedFrame,
    notes: MutableList<String>,
): List<InspectorNode> {
    val bounds = snapshot.liveBounds()
    return ids.mapNotNull { id ->
        val node = InspectorAgentIds.nodeOf(id, snapshot)
        val problem = when {
            node == null -> "no node with this id on screen now; `ui_tree` or `ui_find` gives the ids"
            bounds.getOrNull(node.id) == null -> "takes no space on screen"
            !node.isUnderApp -> "part of Console's own UI, which the image leaves out"
            !frame.shows(node) -> "in a dialog or a popup, which the image leaves out"
            else -> return@mapNotNull node
        }
        notes += listOfNotNull("#$id", node?.name).joinToString(separator = " ") + ": $problem."
        null
    }
}

/** Whether [node] is app content drawn in [this] frame's window, not in a dialog, a popup or Console's own UI. */
private fun CapturedFrame.shows(node: InspectorNode): Boolean {
    return node.isUnderApp && node.primaryLayout?.let(::sharesWindow) == true
}

private fun refreshed(): InspectorSnapshot? {
    InspectorEngine.refresh(force = true)
    val snapshot = InspectorEngine.snapshot.value
    InspectorAgentIds.retain(snapshot)
    return snapshot.takeIf { it.nodes.isNotEmpty() }
}

private fun offHint(config: InspectorConfig): String {
    if (config.enabled) return ""
    return "\nThe inspector is off, so lists, dialogs and other sub-compositions are missing; $TURN_ON turns it on."
}
