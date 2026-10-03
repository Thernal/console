@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.inspector.ui.addon

import io.thernal.console.api.addon.ConsoleAddon
import io.thernal.console.api.addon.ConsoleContentWrapper
import io.thernal.console.api.addon.ConsoleDockWidget
import io.thernal.console.api.addon.ConsoleOverlay
import io.thernal.console.api.addon.ConsoleOverlayLayer
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.inspector.ui.host.InspectorHost
import io.thernal.console.inspector.ui.navigation.InspectorTab
import io.thernal.console.inspector.ui.dock.InspectorDockWidget
import io.thernal.console.inspector.ui.overlay.InspectorBackdrop
import io.thernal.console.inspector.ui.overlay.InspectorCapture
import io.thernal.console.settings.SettingsRegistry

/** UI inspector: composable tree, bounds, recomposition tracking and display overrides. */
object InspectorAddon : ConsoleAddon {
    override fun onInstall() {
        enableInspectorSourceInformation()
        SettingsRegistry.register(inspectorSettingsSection())
    }

    override fun tab(): ConsoleTab = InspectorTab

    override fun overlays(): List<ConsoleOverlay> {
        return listOf(
            ConsoleOverlay(layer = ConsoleOverlayLayer.Backdrop, widgetId = InspectorDockWidget.id) {
                InspectorBackdrop()
            },
            ConsoleOverlay(layer = ConsoleOverlayLayer.Capture, widgetId = InspectorDockWidget.id) {
                InspectorCapture()
            },
        )
    }

    override fun dockWidget(): ConsoleDockWidget = InspectorDockWidget

    override fun contentWrapper(): ConsoleContentWrapper {
        return { content -> InspectorHost(content) }
    }
}
