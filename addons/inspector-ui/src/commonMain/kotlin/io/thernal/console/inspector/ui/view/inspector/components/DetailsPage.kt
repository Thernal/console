package io.thernal.console.inspector.ui.view.inspector.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import io.thernal.console.inspector.ui.engine.recompositionText
import io.thernal.console.inspector.ui.engine.toText
import io.thernal.console.inspector.ui.view.inspector.model.InspectorIntent
import io.thernal.console.inspector.ui.view.inspector.model.InspectorState
import io.thernal.console.ui.common.toTextClipEntry
import kotlinx.coroutines.launch

@Composable
internal fun DetailsPage(
    state: InspectorState,
    dispatch: (InspectorIntent) -> Unit,
) {
    val details = state.details.value
    if (details == null) {
        EmptyHint("Select a composable in the tree, or use the inspect button and tap the app UI.")
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
        ListSection(title = "Modifiers", items = details.modifiers)
        ListSection(title = "Parameters", items = details.parameterNames)
        ListSection(title = "Slot values", items = details.slotValues)
        Row(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8)) {
            DsButton(onClick = { dispatch(InspectorIntent.SelectParent) }) {
                DsText(text = "Select parent", style = Theme.typography.label01)
            }
            DsButton(onClick = { scope.launch { clipboard.setClipEntry(details.toText().toTextClipEntry()) } }) {
                DsText(text = "Copy", style = Theme.typography.label01)
            }
        }
    }
}

private fun detailRows(details: NodeDetails): List<Pair<String, String>> = listOfNotNull(
    "Name" to details.name,
    details.definedIn?.let { "Source file" to it },
    details.parentName?.let { "Parent" to it },
    details.sizeDp?.let { "Size" to it },
    details.positionDp?.let { "Position" to it },
    details.padding?.let { "Padding" to it },
    details.boundsPx?.let { "Bounds (px)" to it },
    details.sizePx?.let { "Size (px)" to it },
    details.density?.let { "Density" to it },
    details.layoutDirection?.let { "Direction" to it },
    "Recompositions" to details.recompositionText(),
    details.skippedPasses?.let { "Skipped" to it.toString() },
    details.lastReason?.let { "Last reason" to it },
)
