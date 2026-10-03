package io.thernal.console.api.addon

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

/**
 * Full-screen overlay layers, bottom to top, all above the app and below the dock and the console:
 * [Backdrop] draws over the app without handling touches, [Capture] may take over touches.
 */
enum class ConsoleOverlayLayer {
    Backdrop,
    Capture,
}

/**
 * A layered full-screen overlay. When [widgetId] names a dock widget, the overlay follows that
 * widget's visibility, so hiding the widget hides what it draws too.
 */
@Immutable
class ConsoleOverlay(
    val layer: ConsoleOverlayLayer,
    val order: Int = 0,
    val widgetId: String? = null,
    val content: @Composable BoxScope.() -> Unit,
)
