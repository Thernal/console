package io.thernal.console.inspector.ui.host

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.GraphicsContext
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.LayoutInfo
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import io.thernal.console.inspector.ui.overlay.InspectorDrawStyle
import kotlin.math.roundToInt

/**
 * One frame of the app content that [ContentCapture] recorded, ready to become an image with an overlay on it. Valid
 * until the host draws its next requested frame. Main thread only.
 */
internal class CapturedFrame(
    private val layer: GraphicsLayer,
    private val graphics: GraphicsContext,
    val style: InspectorDrawStyle,
    val measurer: TextMeasurer,
    val density: Density,
    private val layoutDirection: LayoutDirection,
    private val coordinates: LayoutCoordinates,
) {
    /** The content's size in pixels. */
    val size: IntSize
        get() = layer.size

    /** Whether [layout] is drawn in this frame's window rather than in a dialog's or a popup's. */
    fun sharesWindow(layout: LayoutInfo): Boolean {
        return runCatching {
            layout.isAttached && layout.coordinates.findRootCoordinates() === coordinates.findRootCoordinates()
        }.getOrDefault(false)
    }

    /**
     * The frame scaled by [scale], with [overlay] drawn over it in window pixels (where the inspector's bounds live)
     * at the screen's own density, so it looks as it does on the screen.
     */
    suspend fun render(
        scale: Float,
        overlay: DrawScope.() -> Unit,
    ): ImageBitmap {
        val origin = coordinates.positionInWindow()
        val target = IntSize(
            width = (size.width * scale).roundToInt().coerceAtLeast(1),
            height = (size.height * scale).roundToInt().coerceAtLeast(1),
        )
        val composite = graphics.createGraphicsLayer()
        try {
            composite.record(density = density, layoutDirection = layoutDirection, size = target) {
                scale(scale = scale, pivot = Offset.Zero) {
                    drawLayer(layer)
                    translate(left = -origin.x, top = -origin.y) { overlay() }
                }
            }
            return composite.toImageBitmap()
        } finally {
            graphics.releaseGraphicsLayer(composite)
        }
    }
}
