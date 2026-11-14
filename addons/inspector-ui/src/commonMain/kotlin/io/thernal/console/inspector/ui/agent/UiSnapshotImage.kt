package io.thernal.console.inspector.ui.agent

import androidx.compose.ui.graphics.ImageBitmap

/** One drawn `ui_snapshot`: the [image] at [scale] of the screen's pixels, or null with the reason in [text]. */
internal class UiSnapshotImage(
    val image: ImageBitmap?,
    val text: String,
    val scale: Float,
)
