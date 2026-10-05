package io.thernal.console.inspector.ui.view.inspector.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.inspector.ui.view.inspector.model.InspectorPage

@Composable
internal fun InspectorPageSelector(
    selected: InspectorPage,
    onSelect: (InspectorPage) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Theme.metrics.screenPaddingHorizontal,
                vertical = Theme.dimens.dp8,
            ),
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
    ) {
        InspectorPage.entries.forEach { page ->
            DsChip(
                modifier = Modifier.clickable { onSelect(page) },
                label = page.label,
                selected = page == selected,
            )
        }
    }
}
