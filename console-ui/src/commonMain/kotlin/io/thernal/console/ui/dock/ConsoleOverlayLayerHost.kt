package io.thernal.console.ui.dock

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import io.thernal.console.api.addon.ConsoleDock
import io.thernal.console.api.addon.ConsoleOverlayLayer
import io.thernal.console.api.addon.ConsoleOverlays

/** Draws every shown overlay of [layer], lowest order first. */
@Composable
internal fun BoxScope.ConsoleOverlayLayerHost(layer: ConsoleOverlayLayer) {
    ConsoleOverlays.layer(layer).forEach { overlay ->
        key(overlay) {
            if (overlay.isShown()) overlay.content(this@ConsoleOverlayLayerHost)
        }
    }
}

/** Overlays registered through the legacy `ConsoleAddon.overlay()`: only the dock-wide switch hides them. */
@Composable
internal fun BoxScope.LegacyOverlayHost() {
    val config by ConsoleDock.config.collectAsState()
    if (!config.isVisible) return
    ConsoleOverlays.overlays.forEach { overlay -> overlay() }
}
