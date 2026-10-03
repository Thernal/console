package io.thernal.console.ui.dock

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.api.addon.ConsoleDockWidget
import io.thernal.console.designsystem.foundation.theme.Theme

/** The drag grip, one tab per widget (scrolling when there are many) and the collapse button. */
@Composable
internal fun DockHeader(
    widgets: List<ConsoleDockWidget>,
    selected: ConsoleDockWidget,
    gripModifier: Modifier,
    onSelect: (ConsoleDockWidget) -> Unit,
    onCollapse: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Theme.dimens.dp4, vertical = Theme.dimens.dp4),
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DockGrip(gripModifier)
        Row(
            modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4),
        ) {
            widgets.forEach { widget ->
                DockTab(widget = widget, isSelected = widget.id == selected.id, onClick = { onSelect(widget) })
            }
        }
        DockCollapseButton(onClick = onCollapse)
    }
}
