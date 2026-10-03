package io.thernal.console.ui.dock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.thernal.console.api.addon.ConsoleDockWidget
import io.thernal.console.designsystem.components.core.DsDivider

private val cardWidth = 264.dp
private const val CARD_HEIGHT_FRACTION = 0.8f

/** The open dock: tabs on top, the selected widget's controls below. The width is fixed so nothing jumps around. */
@Composable
internal fun DockCard(
    widgets: List<ConsoleDockWidget>,
    selected: ConsoleDockWidget,
    maxWidth: Dp,
    maxHeight: Dp,
    gripModifier: Modifier,
    onIntent: (DockIntent) -> Unit,
) {
    Column(Modifier.width(minOf(cardWidth, maxWidth))) {
        DockHeader(
            widgets = widgets,
            selected = selected,
            gripModifier = gripModifier,
            onSelect = { onIntent(DockIntent.SelectWidget(it.id)) },
            onCollapse = { onIntent(DockIntent.SetMinimized(true)) },
        )
        DsDivider()
        DockContent(widgets = widgets, selected = selected, maxHeight = maxHeight * CARD_HEIGHT_FRACTION)
    }
}
