package io.thernal.console.inspector.ui.agent

import io.thernal.console.agent.AgentCall
import io.thernal.console.agent.AgentEntries
import io.thernal.console.agent.AgentEntriesScope
import io.thernal.console.agent.AgentImage
import io.thernal.console.agent.agentEntries
import io.thernal.console.inspector.ConsoleInspector
import io.thernal.console.inspector.InspectorFontScale
import io.thernal.console.inspector.InspectorLayoutDirection
import io.thernal.console.inspector.ui.engine.InspectorEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

private const val DEFAULT_TREE_LIMIT = 150
private const val MAX_TREE_LIMIT = 1_000
private const val DEFAULT_FIND_LIMIT = 30
private const val MAX_FIND_LIMIT = 200
private const val DEFAULT_RANKING_LIMIT = 20
private const val MAX_RANKING_LIMIT = 100
private const val MAX_DEPTH = 200
private const val DEFAULT = "default"
private const val SYSTEM = "system"
private const val MIN_SCALE_PERCENT = 5
private const val MAX_SCALE_PERCENT = 100
private const val PERCENT = 100f
private const val SNAPSHOT_NAME = "ui_snapshot"

/** Keeps the PNG, base64 in a frame of at most 4 MB to the hub, well inside it. */
private const val MAX_PNG_BYTES = 2_500_000
private const val BYTES_PER_KILOBYTE = 1_000
private const val MAX_SNAPSHOT_ATTEMPTS = 3

/** Aims a little below the limit when shrinking, since PNG sizes do not follow the pixel count exactly. */
private const val SHRINK_MARGIN = 0.9f

/**
 * What the UI inspector shares with an AI agent: the composable tree on screen with source files, bounds and texts,
 * one node in full, a search, a snapshot of the screen with the overlay, the recomposition ranking, and the font scale
 * and layout direction overrides. The engine lives on the main thread, so every body reads there.
 */
internal fun inspectorAgentEntries(): AgentEntries {
    return agentEntries(
        id = "inspector",
        title = "UI inspector",
        about = "The composable tree on screen, with source files, bounds, texts and recomposition counts, and " +
            "snapshots of the screen with nodes marked",
    ) {
        treeTool()
        tool(name = "ui_node", description = "One node in full: size, position, padding, modifiers, texts, children") {
            val id = int(key = "id", description = "Node id from `ui_tree` or `ui_find`", default = 0, min = 0)
            run { call -> onMain { uiNode(call[id]) } }
        }
        tool(name = "ui_find", description = "Nodes whose name, source file, text or test tag contains a text") {
            val text = text(key = "text", description = "Text to look for, ignoring case")
            val limit = int(
                key = "limit",
                description = "Most nodes",
                default = DEFAULT_FIND_LIMIT,
                min = 1,
                max = MAX_FIND_LIMIT,
            )
            run { call -> onMain { uiFind(call[text], call[limit]) } }
        }
        snapshotTool()
        tool(name = "recompositions", description = "The most recomposed nodes since the last reset, with reasons") {
            val limit = int(
                key = "limit",
                description = "Most nodes",
                default = DEFAULT_RANKING_LIMIT,
                min = 1,
                max = MAX_RANKING_LIMIT,
            )
            run { call -> onMain { recompositionsText(call[limit]) } }
        }
        environmentActions()
        action(name = "recompositions_reset", description = "Resets the recomposition counts and timings") {
            run {
                onMain { InspectorEngine.resetStats() }
                "Recomposition counts reset."
            }
        }
    }
}

private fun AgentEntriesScope.treeTool() {
    tool(
        name = "ui_tree",
        description = "The composable tree on screen, one line per node: id, name, source file, bounds in dp, texts " +
            "and recompositions",
    ) {
        val query = optionalText(
            key = "query",
            description = "Only nodes whose name or file contains this, with what is inside them",
        )
        val depth = optionalInt(
            key = "depth",
            description = "Levels below the top (or each match) to show",
            min = 0,
            max = MAX_DEPTH,
        )
        val framework = toggle(key = "framework", description = "Show Compose's own nodes (Column, Text, …) too")
        val limit = int(
            key = "limit",
            description = "Most lines",
            default = DEFAULT_TREE_LIMIT,
            min = 1,
            max = MAX_TREE_LIMIT,
        )
        run { call -> onMain { uiTree(call[query], call[depth], call[framework], call[limit]) } }
    }
}

private fun AgentEntriesScope.snapshotTool() {
    tool(
        name = "ui_snapshot",
        description = "A PNG of the app's screen as it is now, with the inspector's overlay on the nodes named: " +
            "frame, padding, margin and size in dp, optionally the recomposition heatmap and ×N counters. The hub " +
            "saves it on this computer and names the file, so the person can open it",
    ) {
        val nodes = optionalText(
            key = "nodes",
            description = "Ids from `ui_tree` or `ui_find` to mark, comma-separated; none for a plain screenshot",
        )
        val heatmap = toggle(key = "heatmap", description = "Tint nodes by recompositions, blue to red")
        val counters = toggle(key = "counters", description = "×N recompositions on each node")
        val scale = optionalInt(
            key = "scale",
            description = "Percent of the screen's pixels; by default the long side is about 1600 px",
            min = MIN_SCALE_PERCENT,
            max = MAX_SCALE_PERCENT,
        )
        run { call ->
            snapshot(
                call = call,
                ids = nodeIds(call[nodes]),
                hasHeatmap = call[heatmap],
                hasCounters = call[counters],
                scale = call[scale],
            )
        }
    }
}

/**
 * Draws the snapshot on the main thread and encodes it here, off it; attaches the PNG to [call] and returns the text.
 * A PNG over [MAX_PNG_BYTES] is drawn again smaller.
 */
private suspend fun snapshot(
    call: AgentCall,
    ids: List<Int>,
    hasHeatmap: Boolean,
    hasCounters: Boolean,
    scale: Int?,
): String {
    var drawScale = scale?.let { it / PERCENT }
    repeat(MAX_SNAPSHOT_ATTEMPTS) { attempt ->
        val drawn = onMain { drawUiSnapshot(ids, hasHeatmap, hasCounters, drawScale) }
        val image = drawn.image ?: return drawn.text
        val png = encodePng(image) ?: return drawn.text + "\nThis platform could not encode the image as PNG."
        if (png.size <= MAX_PNG_BYTES) {
            call.attach(AgentImage(bytes = png, name = SNAPSHOT_NAME))
            val limit = MAX_PNG_BYTES / BYTES_PER_KILOBYTE
            return drawn.text + if (attempt > 0) "\nScaled down to stay under $limit KB." else ""
        }
        drawScale = drawn.scale * sqrt(MAX_PNG_BYTES.toFloat() / png.size) * SHRINK_MARGIN
    }
    return "The image stayed over ${MAX_PNG_BYTES / BYTES_PER_KILOBYTE} KB even scaled down; pass a smaller `scale`."
}

private fun AgentEntriesScope.environmentActions() {
    action(name = "ui_font_scale", description = "Scales the app's fonts over the system setting, or stops doing so") {
        val scale = choice(
            key = "scale",
            description =
            "default, or a scale: " + InspectorFontScale.entries.joinToString { "${it.name} ${it.factor}×" },
            options = listOf(DEFAULT) + InspectorFontScale.entries.map { it.name },
            default = DEFAULT,
        )
        run { call ->
            val chosen = InspectorFontScale.entries.firstOrNull { it.name.equals(call[scale], ignoreCase = true) }
            ConsoleInspector.updateConfig { copy(fontScale = chosen) }
            val text = chosen?.let { "${it.name} (${it.factor}× the system's)" } ?: "the system's"
            "Font scale: $text. `ui_tree` shows the new layout once it is drawn; not saved."
        }
    }
    action(
        name = "ui_layout_direction",
        description = "Forces the app left-to-right or right-to-left, or follows the system",
    ) {
        val direction = choice(
            key = "direction",
            description = "system, ltr or rtl",
            options = listOf(SYSTEM) + InspectorLayoutDirection.entries.map { it.name.lowercase() },
            default = SYSTEM,
        )
        run { call ->
            val chosen = InspectorLayoutDirection.entries.firstOrNull {
                it.name.equals(
                    call[direction],
                    ignoreCase = true,
                )
            }
            ConsoleInspector.updateConfig { copy(layoutDirection = chosen) }
            val text = chosen?.name?.uppercase() ?: "the system's"
            "Layout direction: $text. `ui_tree` shows the new layout once it is drawn; not saved."
        }
    }
}

private suspend fun <T> onMain(block: suspend () -> T): T {
    return withContext(Dispatchers.Main) { block() }
}
