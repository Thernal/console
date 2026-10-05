package io.thernal.console.inspector.ui.view.inspector.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import io.thernal.console.designsystem.components.core.DsButton
import io.thernal.console.designsystem.components.core.DsContainer
import io.thernal.console.designsystem.components.core.DsDivider
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.inspector.ui.engine.NodeDetails
import io.thernal.console.inspector.ui.view.inspector.recompositionText
import io.thernal.console.inspector.ui.view.inspector.toText
import io.thernal.console.inspector.ui.view.inspector.model.InspectorIntent
import io.thernal.console.inspector.ui.view.inspector.model.InspectorState
import io.thernal.console.ui.common.toTextClipEntry
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun InspectorDetailsPage(
    state: InspectorState,
    dispatch: (InspectorIntent) -> Unit,
) {
    val details = state.details.value
    if (details == null) {
        InspectorEmptyState("Select a composable in the tree, or use the inspect button and tap the app UI.")
        return
    }
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Theme.metrics.screenPaddingHorizontal),
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp12),
    ) {
        DsContainer(Modifier.fillMaxWidth()) {
            Column {
                detailRows(details).forEachIndexed { index, (label, value) ->
                    if (index > 0) DsDivider()
                    InspectorDetailRow(label = label, value = value)
                }
            }
        }
        InspectorListSection(title = "Modifiers", items = details.modifiers)
        InspectorListSection(title = "Parameters", items = details.parameterNames)
        InspectorListSection(title = "Slot values", items = details.slotValues)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
        ) {
            DsButton(onClick = { dispatch(InspectorIntent.SelectParent) }) {
                DsText(text = "Select parent", style = Theme.typography.label01)
            }
            if (details.isAnchor) {
                DsButton(onClick = { dispatch(InspectorIntent.ClearAnchor) }) {
                    DsText(text = "Clear anchor", style = Theme.typography.label01)
                }
            } else {
                DsButton(onClick = { dispatch(InspectorIntent.MeasureFromSelected) }) {
                    DsText(text = "Measure from here", style = Theme.typography.label01)
                }
            }
            DsButton(onClick = { scope.launch { clipboard.setClipEntry(details.toText().toTextClipEntry()) } }) {
                DsText(text = "Copy", style = Theme.typography.label01)
            }
        }
    }
}

private fun detailRows(details: NodeDetails): List<Pair<String, String>> = listOfNotNull(
    "Name" to details.name,
    anchorRow(details),
    details.definedIn?.let { "Source file" to it },
    details.parentName?.let { "Parent" to it },
    details.sizeDp?.let { "Size" to it },
    details.positionDp?.let { "Position" to it },
    details.padding?.let { "Padding" to it },
    details.margin?.let { "Margin" to it },
    details.boundsPx?.let { "Bounds (px)" to it },
    details.sizePx?.let { "Size (px)" to it },
    details.density?.let { "Density" to it },
    details.layoutDirection?.let { "Direction" to it },
    "Recompositions" to details.recompositionText(),
    details.skippedPasses?.let { "Skipped" to it.toString() },
    details.lastReason?.let { "Last reason" to it },
)

/** The distance from the measuring anchor, or a hint when this node is the anchor. */
private fun anchorRow(details: NodeDetails): Pair<String, String>? = when {
    details.isAnchor -> "Anchor" to "Select another composable to see the distance"
    details.distanceFromAnchor != null -> "From ${details.anchorName}" to details.distanceFromAnchor
    else -> null
}
