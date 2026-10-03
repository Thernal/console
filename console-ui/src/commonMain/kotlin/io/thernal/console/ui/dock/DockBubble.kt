package io.thernal.console.ui.dock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import io.thernal.console.api.addon.ConsoleDockStatus
import io.thernal.console.api.addon.ConsoleDockWidget
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.foundation.theme.Theme

/** The round button that opens the card. Its dot shows the most urgent status among the widgets. */
@Composable
internal fun DockBubble(
    widgets: List<ConsoleDockWidget>,
    onClick: () -> Unit,
) {
    val statuses = widgets.map { it.status() }
    val summary = when {
        ConsoleDockStatus.Attention in statuses -> ConsoleDockStatus.Attention
        ConsoleDockStatus.Active in statuses -> ConsoleDockStatus.Active
        else -> ConsoleDockStatus.Idle
    }
    Box(
        modifier = Modifier
            .size(Theme.metrics.minTouchTarget)
            .semantics { contentDescription = "Open floating widgets" }
            .pressable(onPress = onClick),
        contentAlignment = Alignment.Center,
    ) {
        DsIcon(icon = Icons.Outlined.Widgets, size = Theme.metrics.iconMd, color = Theme.colors.content02)
        DockStatusDot(
            status = summary,
            modifier = Modifier.align(Alignment.TopEnd).padding(Theme.dimens.dp6),
        )
    }
}
