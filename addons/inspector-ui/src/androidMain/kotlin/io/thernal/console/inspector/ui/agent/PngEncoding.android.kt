package io.thernal.console.inspector.ui.agent

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

/** PNG ignores the quality; this is its documented lossless value. */
private const val LOSSLESS = 100

internal actual fun encodePng(image: ImageBitmap): ByteArray? {
    val source = image.asAndroidBitmap()
    // A layer's image is a hardware bitmap, whose pixels the encoder cannot read; a software copy can.
    val isHardware = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && source.config == Bitmap.Config.HARDWARE
    val bitmap = if (isHardware) source.copy(Bitmap.Config.ARGB_8888, false) ?: return null else source
    val stream = ByteArrayOutputStream()
    return if (bitmap.compress(Bitmap.CompressFormat.PNG, LOSSLESS, stream)) stream.toByteArray() else null
}
