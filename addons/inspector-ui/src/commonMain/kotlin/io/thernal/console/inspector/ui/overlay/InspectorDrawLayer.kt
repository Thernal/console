package io.thernal.console.inspector.ui.overlay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.rememberTextMeasurer
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.inspector.ConsoleInspector
import io.thernal.console.inspector.ui.engine.InspectorEngine
import io.thernal.console.inspector.ui.engine.InspectorMeasure

/** Draw-only layer: bounds, labels, heatmap, counters, recomposition flashes, selection and measurements. */
@Composable
internal fun InspectorDrawLayer() {
    val config by ConsoleInspector.config.collectAsState()
    val snapshot by InspectorEngine.snapshot.collectAsState()
    val selectedKey by InspectorEngine.selectedKey.collectAsState()
    val anchorKey by InspectorMeasure.anchorKey.collectAsState()
    val counts by InspectorEngine.nodeCounts.collectAsState()
    val flashes = InspectorEngine.flashes
    val nodes = remember(snapshot, config.hideFrameworkNodes) { snapshot.visibleNodes(config.hideFrameworkNodes) }
    val selected = remember(snapshot, selectedKey) { snapshot.nodeByKey(selectedKey) }
    val anchor = remember(snapshot, anchorKey) { snapshot.nodeByKey(anchorKey) }

    val hasLiveContent = config.showBounds || config.showHeatmap || config.showLabels ||
        config.showRecompositionCounters || flashes.isNotEmpty() || selected != null || anchor != null
    val tick by rememberFrameTick(isActive = hasLiveContent)
    val measurer = rememberTextMeasurer()
    var origin by remember { mutableStateOf(Offset.Zero) }
    val colors = Theme.colors
    val style = remember(colors) {
        InspectorDrawStyle(
            bounds = colors.info,
            flash = colors.warning,
            selection = colors.primary01,
            anchor = colors.success,
            padding = colors.warning,
            measurement = colors.danger,
            labelBackground = colors.background1,
            labelText = Theme.typography.label03.copy(color = colors.content01),
            heat = HeatColors(cold = colors.info, warm = colors.warning, hot = colors.danger),
        )
    }
    val scene = InspectorDrawScene(
        config = config,
        snapshot = snapshot,
        nodes = nodes,
        selected = selected,
        anchor = anchor,
        counts = counts,
        flashes = flashes,
        nowMillis = InspectorEngine::nowMillis,
    )

    Canvas(
        Modifier
            .fillMaxSize()
            .onGloballyPositioned { origin = it.positionInWindow() },
    ) {
        tick
        translate(left = -origin.x, top = -origin.y) {
            drawInspectorScene(scene, style, measurer)
        }
    }
}
