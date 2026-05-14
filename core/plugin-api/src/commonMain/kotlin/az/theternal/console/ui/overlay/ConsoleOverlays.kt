package az.theternal.console.ui.overlay

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf

object ConsoleOverlays {
    private val _overlays = mutableStateListOf<@Composable BoxScope.() -> Unit>()
    val overlays: List<@Composable BoxScope.() -> Unit> get() = _overlays

    fun register(overlay: @Composable BoxScope.() -> Unit) {
        _overlays.add(overlay)
    }
}
