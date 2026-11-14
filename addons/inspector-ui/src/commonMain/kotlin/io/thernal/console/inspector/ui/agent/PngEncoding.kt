package io.thernal.console.inspector.ui.agent

import androidx.compose.ui.graphics.ImageBitmap

/** [image] as PNG bytes, or null where the platform cannot encode it. Off the main thread: it takes a while. */
internal expect fun encodePng(image: ImageBitmap): ByteArray?
