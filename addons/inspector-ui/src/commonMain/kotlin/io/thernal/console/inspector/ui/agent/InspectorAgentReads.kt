package io.thernal.console.inspector.ui.agent

import io.thernal.console.inspector.ConsoleInspector
import io.thernal.console.inspector.InspectorConfig
import io.thernal.console.inspector.ui.engine.InspectorEngine
import io.thernal.console.inspector.ui.engine.InspectorQueries
import io.thernal.console.inspector.ui.engine.tree.InspectorSnapshot
import io.thernal.console.inspector.ui.view.inspector.toText

private const val NO_UI = "No UI on screen to inspect yet."
private const val MAX_CHILDREN = 30
private const val TURN_ON = "settings_set(section=\"inspector\", key=\"enabled\", value=\"true\")"

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
