package io.thernal.console.inspector.ui.dock

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import io.thernal.console.api.addon.ConsoleDockStatus
import io.thernal.console.api.addon.ConsoleDockWidget
import io.thernal.console.inspector.ConsoleInspector
import io.thernal.console.inspector.ui.engine.InspectorEngine
import io.thernal.console.inspector.ui.engine.InspectorMeasure
import io.thernal.console.inspector.ui.engine.geometry.measureDistance
import io.thernal.console.inspector.ui.engine.tree.InspectorNode
import io.thernal.console.inspector.ui.engine.tree.InspectorSnapshot

/** The inspector's place in the console dock. It is hidden unless quick controls are on or a pick is active. */
internal object InspectorDockWidget : ConsoleDockWidget {
    override val id = "inspector"
    override val title = "Inspector"
    override val icon: ImageVector = Icons.Outlined.AccountTree

    @Composable
    override fun isAvailable(): Boolean {
        val config by ConsoleInspector.config.collectAsState()
        val isInspecting by InspectorEngine.inspectMode.collectAsState()
        return config.enabled && (config.showQuickControls || isInspecting)
    }

    @Composable
    override fun status(): ConsoleDockStatus {
        val config by ConsoleInspector.config.collectAsState()
        val isInspecting by InspectorEngine.inspectMode.collectAsState()
        val isFrozen by InspectorEngine.isFrozen.collectAsState()
        return when {
            isInspecting -> ConsoleDockStatus.Attention
            inspectorActiveFeatures(config, isFrozen).isNotEmpty() -> ConsoleDockStatus.Active
            else -> ConsoleDockStatus.Idle
        }
    }

    @Composable
    override fun summary(): String {
        val config by ConsoleInspector.config.collectAsState()
        val isInspecting by InspectorEngine.inspectMode.collectAsState()
        val isFrozen by InspectorEngine.isFrozen.collectAsState()
        val snapshot = InspectorEngine.snapshot.collectAsState()
        val selectedKey = InspectorEngine.selectedKey.collectAsState()
        val isMeasuring by InspectorMeasure.isMeasuring.collectAsState()
        val anchorKey = InspectorMeasure.anchorKey.collectAsState()
        val density = LocalDensity.current.density
        // Read inside derivedStateOf, so a rebuilt tree only recomposes the dock when the line itself changes.
        val pickLine by remember(density) {
            derivedStateOf { pickLine(snapshot.value, selectedKey.value, anchorKey.value, density) }
        }
        return inspectorSummary(
            isInspecting = isInspecting,
            selectedName = pickLine.selectedName,
            activeFeatures = inspectorActiveFeatures(config, isFrozen),
            measureText = if (isMeasuring) pickLine.measureText else null,
        )
    }

    @Composable
    override fun QuickAction() {
        InspectorQuickAction()
    }

    @Composable
    override fun Panel() {
        InspectorPanel()
    }
}

/** Compared by value, so `derivedStateOf` only notifies when the text itself changes. */
private data class PickLine(
    val selectedName: String?,
    val measureText: String,
)

private fun pickLine(
    snapshot: InspectorSnapshot,
    selectedKey: Any?,
    anchorKey: Any?,
    density: Float,
): PickLine {
    val selected = snapshot.nodeByKey(selectedKey)
    val anchor = snapshot.nodeByKey(anchorKey)
    val target = selected?.takeIf { it != anchor }
    val measureText = measureSummary(anchor?.name, target?.name, distanceText(snapshot, anchor, target, density))
    return PickLine(selected?.name, measureText)
}

private fun distanceText(
    snapshot: InspectorSnapshot,
    anchor: InspectorNode?,
    target: InspectorNode?,
    density: Float,
): String? {
    if (anchor == null || target == null) return null
    val bounds = snapshot.liveBounds()
    val first = bounds[anchor.id] ?: return null
    val second = bounds[target.id] ?: return null
    return measureDistance(first, second).describe(density)
}
