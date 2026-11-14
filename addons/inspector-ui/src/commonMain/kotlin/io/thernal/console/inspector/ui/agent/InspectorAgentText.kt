package io.thernal.console.inspector.ui.agent

import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt

private const val INDENT = "  "
private const val PERCENT = 100
private val ID_SEPARATORS = Regex("[\\s,;]+")
private const val MARKED_LEGEND = "Marked (filled frame; padding amber inside it, margin fainter outside; sizes in dp" +
    "; with one node, dashed lines to its parent's edges):"

/** `#12 CartRow (BrewScreen.kt) @16,320 360×56 "Total" "$8.50" ×3`, indented by depth when [indent] is set. */
internal fun uiLine(
    line: UiNodeLine,
    indent: Boolean = true,
): String {
    return buildString {
        if (indent) append(INDENT.repeat(line.depth))
        append('#').append(line.id).append(' ').append(line.name)
        line.file?.let { append(" (").append(it).append(')') }
        line.bounds?.let { append(" @").append(it) }
        line.texts.forEach { append(" \"").append(it).append('"') }
        if (line.recompositions > 0) append(" ×").append(line.recompositions)
    }
}

/**
 * The rows of [lines] that are, or sit inside, a node whose name or file contains [query] (all rows without one),
 * at most [depth] levels below the row that matched. Depths are shifted so each match starts at 0. [lines] must be in
 * tree order, each node before its descendants.
 */
internal fun selectRows(
    lines: List<UiNodeLine>,
    query: String?,
    depth: Int?,
): List<UiNodeLine> {
    val result = mutableListOf<UiNodeLine>()
    var base: Int? = null
    lines.forEach { line ->
        val inside = base?.let { line.depth > it } == true
        if (!inside) base = null
        val matches = query.isNullOrBlank() || line.name.contains(query, ignoreCase = true) ||
            line.file?.contains(query, ignoreCase = true) == true
        if (base == null && matches) base = line.depth
        val start = base ?: return@forEach
        val relative = line.depth - start
        if (depth == null || relative <= depth) result += line.copy(depth = relative)
    }
    return result
}

/** The `ui_tree` answer: a header, then at most [limit] lines and how many were left out. */
internal fun treeText(
    header: String,
    lines: List<UiNodeLine>,
    limit: Int,
): String {
    val shown = lines.take(limit).map { uiLine(it) }
    val hidden = lines.size - shown.size
    val more = if (hidden > 0) listOf("… $hidden more nodes; narrow with `query` or `depth`") else emptyList()
    return (listOf(header) + shown + more).joinToString(separator = "\n")
}

/** `12, #15 20` → `[12, 15, 20]`: the node ids an agent passes, in order, each once; empty for none. */
internal fun nodeIds(text: String?): List<Int> {
    if (text.isNullOrBlank()) return emptyList()
    return text.trim().split(ID_SEPARATORS).map { token ->
        requireNotNull(token.removePrefix("#").toIntOrNull()) {
            "\"$token\" is not a node id; pass ids from `ui_tree` or `ui_find`, such as \"12, 15\""
        }
    }.distinct()
}

/**
 * The `ui_snapshot` answer: `Screen 402×874 dp (1206×2622 px, 3.0 px per dp); image 737×1600 px, 61%.`, the marked
 * nodes as `ui_find` lines, then [notes].
 */
internal fun snapshotText(
    screen: IntSize,
    density: Float,
    image: IntSize,
    scale: Float,
    marked: List<UiNodeLine>,
    notes: List<String>,
): String {
    val dpWidth = (screen.width / density).roundToInt()
    val dpHeight = (screen.height / density).roundToInt()
    val header = "Screen $dpWidth×$dpHeight dp (${screen.width}×${screen.height} px, $density px per dp); image " +
        "${image.width}×${image.height} px, ${(scale * PERCENT).roundToInt()}%."
    val nodes = if (marked.isEmpty()) emptyList() else listOf(MARKED_LEGEND) + marked.map { uiLine(it, indent = false) }
    return (listOf(header) + nodes + notes).joinToString(separator = "\n")
}
