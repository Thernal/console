package io.thernal.console.inspector.ui.agent

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.InspectableValue
import io.thernal.console.inspector.ui.engine.InspectorEngine
import io.thernal.console.inspector.ui.engine.tree.InspectorNode
import io.thernal.console.inspector.ui.engine.tree.InspectorSnapshot
import kotlin.math.roundToInt

private const val MAX_TEXTS = 3
private const val MAX_TEXT_LENGTH = 60
private const val MAX_ANCESTORS = 2
private const val TEST_TAG = "testTag"
private const val TAG_PROPERTY = "tag"

/**
 * Reads one fresh [snapshot] for the agent tools: lines, texts, test tags and ancestors. Built and used on the main
 * thread right after a refresh, while the snapshot's groups are still valid. Texts come from the slot table (values
 * that can change) and, where the platform shares it, the semantics tree (literals too).
 */
internal class UiTreeReader(
    private val snapshot: InspectorSnapshot,
    private val hideFramework: Boolean,
) {
    private val bounds = snapshot.liveBounds()
    private val counts = InspectorEngine.nodeCounts.value
    private val semantics = platformSemanticsTexts()

    /** Pixels per dp, from the first node with a layout. */
    val density: Float = snapshot.nodes.firstNotNullOfOrNull { it.primaryLayout?.density?.density } ?: 1f

    /** The visible nodes in tree order, each with its depth among visible ancestors. */
    fun rows(): List<UiNodeLine> {
        return snapshot.rows(hideFramework).map { (node, depth) -> line(node, depth) }
    }

    fun line(
        node: InspectorNode,
        depth: Int = node.depth,
        ownTextsOnly: Boolean = false,
    ): UiNodeLine {
        return UiNodeLine(
            id = InspectorAgentIds.idOf(node),
            depth = depth,
            name = node.name,
            file = node.file,
            bounds = bounds.getOrNull(node.id)?.let(::dpText),
            recompositions = counts[node.id] ?: 0,
            texts = texts(node, ownTextsOnly),
        )
    }

    /** Strings [node] holds and, unless [ownOnly], those of the hidden framework nodes right below it. */
    fun texts(
        node: InspectorNode,
        ownOnly: Boolean = false,
    ): List<String> {
        val found = mutableListOf<String>()
        collectTexts(node, found, ownOnly)
        return found.distinct().take(MAX_TEXTS)
    }

    /** Values of `Modifier.testTag` on [node]'s own layouts. */
    fun testTags(node: InspectorNode): List<String> {
        return node.layouts.flatMap { layout ->
            runCatching { layout.getModifierInfo() }.getOrDefault(emptyList()).mapNotNull { info ->
                val inspectable = info.modifier as? InspectableValue ?: return@mapNotNull null
                if (inspectable.nameFallback != TEST_TAG) return@mapNotNull null
                inspectable.inspectableElements.firstOrNull { it.name == TAG_PROPERTY }?.value as? String
                    ?: inspectable.valueOverride as? String
            }
        }
    }

    /** ` in #12 CartRow < #9 CartList`: the nearest shown ancestors, or empty at the top. */
    fun insideText(node: InspectorNode): String {
        val parent = snapshot.nodes.getOrNull(node.parentId)
        val ancestors = generateSequence(parent) { snapshot.nodes.getOrNull(it.parentId) }
            .filter { !(hideFramework && it.isFramework) }
            .take(MAX_ANCESTORS)
            .map { "#${InspectorAgentIds.idOf(it)} ${it.name}" }
            .toList()
        return if (ancestors.isEmpty()) "" else ancestors.joinToString(separator = " < ", prefix = " in ")
    }

    /** The direct children of [node], shown or not. */
    fun children(node: InspectorNode): List<InspectorNode> {
        return node.childIds.mapNotNull(snapshot.nodes::getOrNull)
    }

    private fun collectTexts(
        node: InspectorNode,
        found: MutableList<String>,
        ownOnly: Boolean,
    ) {
        node.group.data.forEach { item -> addText((item as? CharSequence)?.toString(), found) }
        node.layouts.forEach { layout -> semantics[layout.semanticsId]?.forEach { addText(it, found) } }
        if (ownOnly) return
        children(node).filter { hideFramework && it.isFramework }.forEach { collectTexts(it, found, ownOnly = false) }
    }

    private fun addText(
        value: String?,
        found: MutableList<String>,
    ) {
        val text = value?.trim()
        if (!text.isNullOrEmpty() && text.length <= MAX_TEXT_LENGTH) found += text
    }

    private fun dpText(rect: Rect): String {
        return "${dp(rect.left)},${dp(rect.top)} ${dp(rect.width)}×${dp(rect.height)}"
    }

    private fun dp(px: Float): Int {
        return (px / density).roundToInt()
    }
}
