package io.thernal.console.inspector.ui.host

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.GraphicsContext
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.thernal.console.inspector.ui.overlay.InspectorDrawStyle
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull

/**
 * The app content's pixels, for the agent's `ui_snapshot`. The host draws its content straight to the screen; asked
 * for a frame, it also records that frame into its layer, so the app looks the same and nothing is recorded between
 * snapshots. Only the host's content is in it: Console's own UI, dialogs and popups are drawn elsewhere. Main thread.
 */
internal object ContentCapture {
    private var request by mutableStateOf<CompletableDeferred<Unit>?>(null)
    private var layer: GraphicsLayer? = null
    private var graphics: GraphicsContext? = null
    private var style: InspectorDrawStyle? = null
    private var measurer: TextMeasurer? = null
    private var density: Density? = null
    private var layoutDirection = LayoutDirection.Ltr
    private var coordinates: LayoutCoordinates? = null

    /** Whether a host is on screen to capture. */
    val isAttached: Boolean
        get() = layer != null

    /** Records the host's content into [layer] when a snapshot waits for it; on the host's layout. */
    val modifier: Modifier = Modifier
        .onGloballyPositioned { coordinates = it }
        .drawWithContent { draw(this) }

    /** What the host provides while it is composed; the overlay is drawn with its [style] and [measurer]. */
    fun attach(
        layer: GraphicsLayer,
        graphics: GraphicsContext,
        style: InspectorDrawStyle,
        measurer: TextMeasurer,
        density: Density,
        layoutDirection: LayoutDirection,
    ) {
        this.layer = layer
        this.graphics = graphics
        this.style = style
        this.measurer = measurer
        this.density = density
        this.layoutDirection = layoutDirection
    }

    fun detach(layer: GraphicsLayer) {
        if (this.layer !== layer) return
        this.layer = null
        coordinates = null
    }

    /** The host's next frame, recorded; null when no host is on screen or it drew nothing within [timeoutMillis]. */
    suspend fun nextFrame(timeoutMillis: Long): CapturedFrame? {
        if (layer == null) return null
        val drawn = CompletableDeferred<Unit>()
        request = drawn
        val isDrawn = try {
            withTimeoutOrNull(timeoutMillis) { drawn.await() } != null
        } finally {
            if (request === drawn) request = null
        }
        return if (isDrawn) frame() else null
    }

    private fun frame(): CapturedFrame? {
        val layer = layer
        val graphics = graphics
        val style = style
        val measurer = measurer
        val density = density
        val coordinates = coordinates?.takeIf { it.isAttached }
        val isReady = layer != null && graphics != null && style != null && measurer != null && density != null
        if (!isReady || coordinates == null) return null
        return CapturedFrame(
            layer = layer,
            graphics = graphics,
            style = style,
            measurer = measurer,
            density = density,
            layoutDirection = layoutDirection,
            coordinates = coordinates,
        )
    }

    private fun draw(scope: ContentDrawScope) {
        val waiting = request
        val target = layer
        if (waiting == null || target == null) {
            scope.drawContent()
            return
        }
        with(scope) {
            target.record { scope.drawContent() }
            drawLayer(target)
        }
        waiting.complete(Unit)
    }
}
