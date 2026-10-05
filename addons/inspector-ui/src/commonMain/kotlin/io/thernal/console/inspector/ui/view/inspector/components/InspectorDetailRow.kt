package io.thernal.console.inspector.ui.view.inspector.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun InspectorDetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.dimens.dp12, vertical = Theme.dimens.dp8),
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp16),
        verticalAlignment = Alignment.Top,
    ) {
        DsText(
            text = label,
            style = Theme.typography.label02,
            color = Theme.colors.content04,
            modifier = Modifier.weight(LABEL_WEIGHT),
        )
        DsText(
            text = value,
            style = Theme.typography.body02,
            color = Theme.colors.content01,
            modifier = Modifier.weight(VALUE_WEIGHT),
        )
    }
}

private const val LABEL_WEIGHT = 0.34f
private const val VALUE_WEIGHT = 0.66f
