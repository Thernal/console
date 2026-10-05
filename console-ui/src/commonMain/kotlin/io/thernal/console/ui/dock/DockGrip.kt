package io.thernal.console.ui.dock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.foundation.theme.Theme

private val gripWidth = 20.dp
private val headerHeight = 36.dp

/** Six dots at the start of the rail: the one place to drag the dock from. */
@Composable
internal fun DockGrip(dragModifier: Modifier) {
    Box(
        modifier = dragModifier
            .size(width = gripWidth, height = headerHeight)
            .semantics { contentDescription = "Move floating widgets" },
        contentAlignment = Alignment.Center,
    ) {
        DsIcon(icon = Icons.Default.DragIndicator, size = Theme.metrics.iconSm, color = Theme.colors.content04)
    }
}
