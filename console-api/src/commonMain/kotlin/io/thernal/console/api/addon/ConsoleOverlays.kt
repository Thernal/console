package io.thernal.console.api.addon

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf

object ConsoleOverlays {
    private val _overlays = mutableStateListOf<@Composable BoxScope.() -> Unit>()
    val overlays: List<@Composable BoxScope.() -> Unit> get() = _overlays

    private val layeredOverlays = mutableStateListOf<ConsoleOverlay>()

    /** Layered overlays of [layer], lowest order first. */
    fun layer(layer: ConsoleOverlayLayer): List<ConsoleOverlay> {
        return layeredOverlays.filter { it.layer == layer }
    }

    internal fun register(overlay: ConsoleOverlay) {
        if (layeredOverlays.any { it === overlay }) return
        val index = layeredOverlays.indexOfFirst { it.layer == overlay.layer && it.order > overlay.order }
        if (index == -1) layeredOverlays.add(overlay) else layeredOverlays.add(index, overlay)
    }

    internal fun register(overlay: @Composable BoxScope.() -> Unit) {
        _overlays.add(overlay)
    }
}
