package io.thernal.console.ui.dock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.api.addon.ConsoleDockWidget
import io.thernal.console.designsystem.foundation.theme.Theme

/** The bubble plus the quick actions of every widget that needs attention. It folds back once nothing does. */
@Composable
internal fun DockPill(
    widgets: List<ConsoleDockWidget>,
    attentionWidgets: List<ConsoleDockWidget>,
    onOpen: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        DockBubble(widgets = widgets, onClick = onOpen)
        Row(
            modifier = Modifier.padding(end = Theme.dimens.dp6),
            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            attentionWidgets.forEach { it.QuickAction() }
        }
    }
}
