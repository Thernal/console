package io.thernal.console.inspector.ui.view.inspector.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.inspector.ConsoleInspector
import io.thernal.console.inspector.InspectorCommand
import io.thernal.console.inspector.ui.view.inspector.model.InspectorState

@Composable
internal fun InspectorRecompositionsPage(state: InspectorState) {
    val timing = state.timing.value
    val ranking = state.ranking.value
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.padding(start = Theme.metrics.screenPaddingHorizontal),
            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DsText(
                text = "Compositions ${timing.count} · avg ${timing.averageMicros} µs · " +
                    "max ${timing.maxMicros} µs · last ${timing.lastMicros} µs",
                style = Theme.typography.label02,
                color = Theme.colors.content04,
                modifier = Modifier.weight(1f),
            )
            DsIconButton(onClick = { ConsoleInspector.dispatch(InspectorCommand.ResetStats) }) {
                DsIcon(icon = Icons.Outlined.RestartAlt, color = Theme.colors.content02)
            }
        }
        if (ranking.isEmpty()) {
            InspectorEmptyState(
                "No recompositions recorded. Interact with the app, " +
                    "and make sure tracking is enabled in Settings.",
            )
            return@Column
        }
        LazyColumn(Modifier.fillMaxSize()) {
            items(ranking, key = { it.nodeId }) { entry -> InspectorRecompositionRow(entry) }
        }
    }
}
