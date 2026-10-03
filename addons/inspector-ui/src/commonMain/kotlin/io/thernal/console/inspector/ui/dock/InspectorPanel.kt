package io.thernal.console.inspector.ui.dock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.designsystem.components.core.chip.DsActionChip
import io.thernal.console.designsystem.components.core.chip.DsChipGroup
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.inspector.ConsoleInspector
import io.thernal.console.inspector.InspectorCommand
import io.thernal.console.inspector.InspectorLayoutDirection
import io.thernal.console.inspector.ui.engine.InspectorEngine
import io.thernal.console.inspector.ui.engine.InspectorMeasure
import io.thernal.console.inspector.ui.engine.nextFontScale
import io.thernal.console.inspector.ui.navigation.InspectorTab

/** Every inspector control as a compact chip, grouped by purpose. Active chips are tinted. */
@Composable
internal fun InspectorPanel() {
    val config by ConsoleInspector.config.collectAsState()
    val isInspecting by InspectorEngine.inspectMode.collectAsState()
    val isFrozen by InspectorEngine.isFrozen.collectAsState()
    val isMeasuring by InspectorMeasure.isMeasuring.collectAsState()
    val navigator = LocalConsoleNavigator.current

    Column(verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8)) {
        DsChipGroup(title = "Actions") {
            DsActionChip(
                label = "Inspect",
                icon = Icons.Default.AdsClick,
                isActive = isInspecting,
                onClick = {
                    InspectorEngine.refresh(force = true)
                    InspectorEngine.setInspectMode(!isInspecting)
                },
            )
            DsActionChip(
                label = "Measure distance between two items",
                icon = Icons.Default.Straighten,
                isActive = isMeasuring,
                onClick = {
                    InspectorEngine.refresh(force = true)
                    InspectorMeasure.setMeasuring(!isMeasuring)
                    if (!isMeasuring) InspectorEngine.setInspectMode(true)
                },
            )
            DsActionChip(
                label = "Freeze",
                icon = Icons.Default.AcUnit,
                isActive = isFrozen,
                onClick = { InspectorEngine.setFrozen(!isFrozen) },
            )
            DsActionChip(
                label = "Tree",
                icon = Icons.Default.AccountTree,
                onClick = { navigator.openTab(InspectorTab) },
            )
        }
        DsChipGroup(title = "Overlay") {
            DsActionChip(
                label = "Bounds",
                icon = Icons.Default.GridOn,
                isActive = config.showBounds,
                onClick = { ConsoleInspector.updateConfig { copy(showBounds = !showBounds) } },
            )
            DsActionChip(
                label = "Labels",
                icon = Icons.Default.Label,
                isActive = config.showLabels,
                onClick = { ConsoleInspector.updateConfig { copy(showLabels = !showLabels) } },
            )
            DsActionChip(
                label = "Counters",
                icon = Icons.Default.Tag,
                isActive = config.showRecompositionCounters,
                onClick = {
                    ConsoleInspector.updateConfig { copy(showRecompositionCounters = !showRecompositionCounters) }
                },
            )
            DsActionChip(
                label = "Heatmap",
                icon = Icons.Default.LocalFireDepartment,
                isActive = config.showHeatmap,
                onClick = { ConsoleInspector.updateConfig { copy(showHeatmap = !showHeatmap) } },
            )
            DsActionChip(
                label = "Flash on recomposition",
                icon = Icons.Default.FlashOn,
                isActive = config.highlightRecompositions,
                onClick = {
                    ConsoleInspector.updateConfig { copy(highlightRecompositions = !highlightRecompositions) }
                },
            )
            DsActionChip(
                label = "Reset",
                icon = Icons.Outlined.RestartAlt,
                onClick = { ConsoleInspector.dispatch(InspectorCommand.ResetStats) },
            )
        }
        DsChipGroup(title = "Environment") {
            DsActionChip(
                label = config.fontScale?.let { "Font ×${it.factor}" } ?: "Font auto",
                icon = Icons.Default.FormatSize,
                isActive = config.fontScale != null,
                onClick = { ConsoleInspector.updateConfig { copy(fontScale = nextFontScale(fontScale)) } },
            )
            DsActionChip(
                label = "RTL",
                icon = Icons.Default.SwapHoriz,
                isActive = config.layoutDirection == InspectorLayoutDirection.Rtl,
                onClick = {
                    ConsoleInspector.updateConfig { copy(layoutDirection = toggledDirection(layoutDirection)) }
                },
            )
        }
    }
}

private fun toggledDirection(current: InspectorLayoutDirection?): InspectorLayoutDirection? =
    if (current == InspectorLayoutDirection.Rtl) null else InspectorLayoutDirection.Rtl
