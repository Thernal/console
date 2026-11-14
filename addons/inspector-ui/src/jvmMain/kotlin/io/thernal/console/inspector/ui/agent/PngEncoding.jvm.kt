package io.thernal.console.inspector.ui.agent

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

internal actual fun encodePng(image: ImageBitmap): ByteArray? {
    val skiaImage = Image.makeFromBitmap(image.asSkiaBitmap())
    return try {
        skiaImage.encodeToData(EncodedImageFormat.PNG)?.bytes
    } finally {
        skiaImage.close()
    }
}
