@file:OptIn(ComposeToolingApi::class)

package io.thernal.console.inspector.ui.engine

import androidx.compose.runtime.tooling.ComposeToolingApi
import androidx.compose.runtime.tooling.parseSourceInformation
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.InspectableValue
import kotlin.math.roundToInt

/** Builds [NodeDetails] for a node of a fresh snapshot. */
internal object NodeDetailsReader {
    private const val MAX_SLOT_VALUES = 12
    private const val MAX_VALUE_LENGTH = 40
    private const val MAX_MODIFIERS = 16
    private const val MAX_PROPERTIES = 3
    private const val TENTHS = 10

    fun read(
        snapshot: InspectorSnapshot,
        node: InspectorNode,
        bounds: Rect?,
        recompositions: Int,
        ownRecompositions: Int,
        skippedPasses: Int?,
        lastReason: String?,
    ): NodeDetails {
        val layout = node.primaryLayout
        val density = layout?.density?.density
        val parent = snapshot.nodes.getOrNull(node.parentId)
        val info = node.group.sourceInfo?.let { parseSourceInformation(it) }
        return NodeDetails(
            name = node.name,
            definedIn = node.file,
            parentName = parent?.name,
            boundsPx = bounds?.let(::formatRect),
            sizePx = bounds?.let { "${it.width.roundToInt()} × ${it.height.roundToInt()} px" },
            sizeDp = bounds?.let { rect -> density?.let { "${dp(rect.width, it)} × ${dp(rect.height, it)} dp" } },
            padding = layout?.let(::readNodePadding)?.let { padding -> density?.let(padding::describe) },
            positionDp = bounds?.let { rect -> density?.let { "(${dp(rect.left, it)}, ${dp(rect.top, it)}) dp" } },
            density = layout?.density?.let { "${it.density}x · font ${it.fontScale}x" },
            layoutDirection = layout?.layoutDirection?.name,
            recompositions = recompositions,
            ownRecompositions = ownRecompositions,
            skippedPasses = skippedPasses,
            lastReason = lastReason,
            modifiers = layout?.let(::readModifiers).orEmpty(),
            parameterNames = info?.parameters?.mapNotNull { it.name }.orEmpty(),
            slotValues = node.group.data.take(MAX_SLOT_VALUES).map { shortValue(it) },
        )
    }

    private fun readModifiers(layout: androidx.compose.ui.layout.LayoutInfo): List<String> =
        runCatching { layout.getModifierInfo() }.getOrDefault(emptyList()).take(MAX_MODIFIERS).map { info ->
            val modifier = info.modifier
            val inspectable = modifier as? InspectableValue
            val properties = inspectable?.inspectableElements?.take(MAX_PROPERTIES)
                ?.joinToString(", ") { "${it.name}=${shortValue(it.value)}" }
            val name = inspectable?.nameFallback ?: modifier::class.simpleName ?: "Modifier"
            if (properties.isNullOrEmpty()) name else "$name($properties)"
        }

    private fun shortValue(value: Any?): String {
        val text = (value?.toString() ?: "null").replace('\n', ' ')
        return if (text.length > MAX_VALUE_LENGTH) text.take(MAX_VALUE_LENGTH) + "…" else text
    }

    private fun dp(
        px: Float,
        density: Float,
    ): String {
        val value = px / density
        return if (value % 1f == 0f) value.roundToInt().toString() else value.roundTo(TENTHS).toString()
    }

    private fun Float.roundTo(factor: Int): Float {
        return (this * factor).roundToInt() / factor.toFloat()
    }

    private fun formatRect(rect: Rect): String =
        "${rect.left.roundToInt()}, ${rect.top.roundToInt()} → ${rect.right.roundToInt()}, ${rect.bottom.roundToInt()}"
}
