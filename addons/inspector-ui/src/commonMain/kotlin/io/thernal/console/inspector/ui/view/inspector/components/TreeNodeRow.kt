package io.thernal.console.inspector.ui.view.inspector.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.inspector.ui.view.inspector.model.InspectorRowModel

@Composable
internal fun TreeNodeRow(
    row: InspectorRowModel,
    onClick: () -> Unit,
    onToggle: () -> Unit,
) {
    val node = row.node
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (row.isSelected) Theme.colors.primary01.copy(alpha = SELECTED_ALPHA) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(
                start = Theme.metrics.screenPaddingHorizontal + Theme.dimens.dp12 * row.depth,
                end = Theme.metrics.screenPaddingHorizontal,
                top = Theme.dimens.dp6,
                bottom = Theme.dimens.dp6,
            ),
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (row.hasChildren) {
            DsIcon(
                icon = if (row.isCollapsed) Icons.Default.KeyboardArrowRight else Icons.Default.ExpandMore,
                size = Theme.metrics.iconSm,
                modifier = Modifier.clickable(onClick = onToggle),
            )
        } else {
            Spacer(Modifier.size(Theme.metrics.iconSm))
        }
        Column(Modifier.weight(1f)) {
            DsText(
                text = node.name,
                style = Theme.typography.body02,
                color = if (node.isFramework) Theme.colors.content03 else Theme.colors.content01,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            DsText(
                text = node.file.orEmpty(),
                style = Theme.typography.label02,
                color = Theme.colors.content04,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (row.count > 0) {
            DsText(
                text = "×${row.count}",
                style = Theme.typography.label02,
                color = Theme.colors.warning,
            )
        }
    }
}

private const val SELECTED_ALPHA = 0.18f
