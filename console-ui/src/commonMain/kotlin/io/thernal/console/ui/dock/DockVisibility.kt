package io.thernal.console.ui.dock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.thernal.console.api.addon.ConsoleDock
import io.thernal.console.api.addon.ConsoleDockStatus
import io.thernal.console.api.addon.ConsoleDockWidget
import io.thernal.console.api.addon.ConsoleOverlay

/** Widgets that are available and allowed on screen under the current dock config. */
@Composable
internal fun shownDockWidgets(): List<ConsoleDockWidget> {
    val config by ConsoleDock.config.collectAsState()
    return ConsoleDock.widgets.filter { widget ->
        widget.isAvailable() && config.shows(widget.id, widget.status() == ConsoleDockStatus.Attention)
    }
}

/** Whether a layered overlay is on screen: it follows its widget's visibility, or the dock-wide switch. */
@Composable
internal fun ConsoleOverlay.isShown(): Boolean {
    val config by ConsoleDock.config.collectAsState()
    val widget = widgetId?.let { id -> ConsoleDock.widgets.firstOrNull { it.id == id } }
    return config.shows(widgetId, widget?.status() == ConsoleDockStatus.Attention)
}
