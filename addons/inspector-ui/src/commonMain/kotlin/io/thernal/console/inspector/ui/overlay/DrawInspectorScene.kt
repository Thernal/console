package io.thernal.console.inspector.ui.overlay

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.LayoutInfo
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.sp
import io.thernal.console.inspector.ui.engine.InspectorNode
import io.thernal.console.inspector.ui.engine.measureDistance
import io.thernal.console.inspector.ui.engine.readNodePadding
import kotlin.math.roundToInt

private const val MAX_DRAWN_NODES = 600
private const val MAX_LABELS = 150
private const val BOUNDS_ALPHA = 0.55f
private const val HEAT_ALPHA = 0.35f
private const val FLASH_ALPHA = 0.55f
private const val SELECTION_ALPHA = 0.25f
private const val PADDING_ALPHA = 0.35f
private const val LABEL_BACKGROUND_ALPHA = 0.85f
private const val STROKE_PX = 1f
private const val SELECTION_STROKE_PX = 3f
private const val LABEL_PADDING_PX = 3f
private const val DASH_PX = 6f
private val labelTextSize = 9.sp

internal fun DrawScope.drawInspectorScene(
    scene: InspectorDrawScene,
    style: InspectorDrawStyle,
    measurer: TextMeasurer,
) {
    val config = scene.config
    val liveBounds = scene.snapshot.liveBounds()
    val drawn = scene.nodes.take(MAX_DRAWN_NODES).mapNotNull { node -> liveBounds[node.id]?.let { node to it } }
    var labelCount = 0
    drawn.forEach { (node, bounds) ->
        if (bounds.width <= 0f || bounds.height <= 0f) return@forEach
        val count = scene.counts[node.id] ?: 0
        if (config.showHeatmap && count > 0) {
            drawRect(heatColor(count, config.heatmapThreshold).copy(alpha = HEAT_ALPHA), bounds.topLeft, bounds.size)
        }
        if (config.showBounds) {
            drawRect(style.bounds.copy(alpha = BOUNDS_ALPHA), bounds.topLeft, bounds.size, style = Stroke(STROKE_PX))
        }
        drawFlash(scene, node, bounds, style)
        if (config.showRecompositionCounters && count > 0) {
            drawTag("×$count", Offset(bounds.right, bounds.top), alignRight = true, style, measurer)
        }
        if (config.showLabels && labelCount < MAX_LABELS) {
            labelCount++
            drawTag(node.name, bounds.topLeft, alignRight = false, style, measurer)
        }
    }
    drawPick(scene, liveBounds, style, measurer)
}

/** The picked item, the measuring anchor and the distance between them. */
private fun DrawScope.drawPick(
    scene: InspectorDrawScene,
    liveBounds: List<Rect?>,
    style: InspectorDrawStyle,
    measurer: TextMeasurer,
) {
    scene.anchor?.let { drawAnchor(it, liveBounds, style, measurer) }
    scene.selected?.let { drawSelection(scene, it, liveBounds, style, measurer) }
    val anchorBounds = scene.anchor?.let { liveBounds[it.id] }
    val targetBounds = scene.selected?.takeIf { it != scene.anchor }?.let { liveBounds[it.id] }
    if (anchorBounds != null && targetBounds != null) drawDistance(anchorBounds, targetBounds, style, measurer)
}

private fun DrawScope.drawAnchor(
    node: InspectorNode,
    liveBounds: List<Rect?>,
    style: InspectorDrawStyle,
    measurer: TextMeasurer,
) {
    val bounds = liveBounds[node.id] ?: return
    drawRect(style.anchor.copy(alpha = SELECTION_ALPHA), bounds.topLeft, bounds.size)
    drawRect(style.anchor, bounds.topLeft, bounds.size, style = Stroke(SELECTION_STROKE_PX))
    val tagTop = Offset(bounds.left, bounds.top - tagHeight(measurer))
    drawTag("${node.name} (anchor)", tagTop, alignRight = false, style, measurer)
}

private fun tagHeight(measurer: TextMeasurer): Float =
    measurer.measure("A", TextStyle(fontSize = labelTextSize), maxLines = 1).size.height + 2 * LABEL_PADDING_PX

private fun DrawScope.drawDistance(
    anchor: Rect,
    target: Rect,
    style: InspectorDrawStyle,
    measurer: TextMeasurer,
) {
    val dash = Stroke(STROKE_PX * 2, pathEffect = PathEffect.dashPathEffect(floatArrayOf(DASH_PX, DASH_PX)))
    measureDistance(anchor, target).segments.forEach { segment ->
        drawGap(segment.from, segment.to, dash, style, measurer)
    }
}

private fun DrawScope.drawFlash(
    scene: InspectorDrawScene,
    node: InspectorNode,
    bounds: Rect,
    style: InspectorDrawStyle,
) {
    val started = scene.flashes[node.key] ?: return
    val duration = scene.config.highlightDurationMillis
    val age = scene.nowMillis() - started
    if (age < 0 || age >= duration) return
    val alpha = FLASH_ALPHA * (1f - age.toFloat() / duration)
    drawRect(style.flash.copy(alpha = alpha), bounds.topLeft, bounds.size)
}

private fun DrawScope.drawSelection(
    scene: InspectorDrawScene,
    node: InspectorNode,
    liveBounds: List<Rect?>,
    style: InspectorDrawStyle,
    measurer: TextMeasurer,
) {
    val bounds = liveBounds[node.id] ?: return
    drawRect(style.selection.copy(alpha = SELECTION_ALPHA), bounds.topLeft, bounds.size)
    drawRect(style.selection, bounds.topLeft, bounds.size, style = Stroke(SELECTION_STROKE_PX))
    val widthDp = (bounds.width / density).roundToInt()
    val heightDp = (bounds.height / density).roundToInt()
    val sizeLabel = "${node.name}  $widthDp×$heightDp dp"
    drawTag(sizeLabel, Offset(bounds.left, bounds.bottom), alignRight = false, style, measurer)
    if (scene.config.showMeasurements) node.primaryLayout?.let { drawPadding(it, style, measurer) }
    if (scene.config.showMeasurements && scene.anchor == null) {
        scene.parentBounds(node, liveBounds)?.let { drawMeasurements(bounds, it, style, measurer) }
    }
}

private fun DrawScope.drawPadding(
    layout: LayoutInfo,
    style: InspectorDrawStyle,
    measurer: TextMeasurer,
) {
    val padding = readNodePadding(layout) ?: return
    padding.rings.forEach { ring ->
        val topBand = Rect(ring.outer.left, ring.outer.top, ring.outer.right, ring.inner.top)
        val bottomBand = Rect(ring.outer.left, ring.inner.bottom, ring.outer.right, ring.outer.bottom)
        val leftBand = Rect(ring.outer.left, ring.inner.top, ring.inner.left, ring.inner.bottom)
        val rightBand = Rect(ring.inner.right, ring.inner.top, ring.outer.right, ring.inner.bottom)
        val sides = listOf(
            ring.top to topBand,
            ring.bottom to bottomBand,
            ring.left to leftBand,
            ring.right to rightBand,
        )
        sides.forEach { (_, band) ->
            if (band.width > 0f && band.height > 0f) {
                drawRect(style.padding.copy(alpha = PADDING_ALPHA), band.topLeft, band.size)
            }
        }
        sides.forEach { (px, band) ->
            if (px >= 1f) drawTag("${(px / density).roundToInt()}", band.center, alignRight = false, style, measurer)
        }
    }
}

private fun DrawScope.drawMeasurements(
    inner: Rect,
    outer: Rect,
    style: InspectorDrawStyle,
    measurer: TextMeasurer,
) {
    val dash = Stroke(STROKE_PX, pathEffect = PathEffect.dashPathEffect(floatArrayOf(DASH_PX, DASH_PX)))
    val midX = inner.center.x
    val midY = inner.center.y
    drawGap(Offset(outer.left, midY), Offset(inner.left, midY), dash, style, measurer)
    drawGap(Offset(inner.right, midY), Offset(outer.right, midY), dash, style, measurer)
    drawGap(Offset(midX, outer.top), Offset(midX, inner.top), dash, style, measurer)
    drawGap(Offset(midX, inner.bottom), Offset(midX, outer.bottom), dash, style, measurer)
}

private fun DrawScope.drawGap(
    from: Offset,
    to: Offset,
    stroke: Stroke,
    style: InspectorDrawStyle,
    measurer: TextMeasurer,
) {
    val length = maxOf(kotlin.math.abs(to.x - from.x), kotlin.math.abs(to.y - from.y))
    if (length < 1f) return
    drawLine(style.measurement, from, to, strokeWidth = stroke.width, pathEffect = stroke.pathEffect)
    val label = "${(length / density).roundToInt()}"
    drawTag(label, Offset((from.x + to.x) / 2, (from.y + to.y) / 2), alignRight = false, style, measurer)
}

private fun DrawScope.drawTag(
    text: String,
    anchor: Offset,
    alignRight: Boolean,
    style: InspectorDrawStyle,
    measurer: TextMeasurer,
) {
    val result = measurer.measure(text, TextStyle(fontSize = labelTextSize, color = style.labelText), maxLines = 1)
    val width = result.size.width + 2 * LABEL_PADDING_PX
    val height = result.size.height + 2 * LABEL_PADDING_PX
    val left = if (alignRight) anchor.x - width else anchor.x
    drawRect(style.labelBackground.copy(alpha = LABEL_BACKGROUND_ALPHA), Offset(left, anchor.y), Size(width, height))
    drawText(result, topLeft = Offset(left + LABEL_PADDING_PX, anchor.y + LABEL_PADDING_PX))
}
