package io.thernal.console.inspector.ui.view.inspector.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.inspector.ui.engine.recomposition.RecompositionEntry

@Composable
internal fun InspectorRecompositionRow(entry: RecompositionEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.metrics.screenPaddingHorizontal, vertical = Theme.dimens.dp8),
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = "×${entry.count}",
            style = Theme.typography.label01,
            color = Theme.colors.warning,
        )
        Column(Modifier.weight(1f)) {
            DsText(text = entry.name, style = Theme.typography.body02)
            DsText(
                text = listOfNotNull(entry.file, entry.reason?.let { "← $it" }).joinToString("  "),
                style = Theme.typography.label02,
                color = Theme.colors.content04,
            )
        }
    }
}
