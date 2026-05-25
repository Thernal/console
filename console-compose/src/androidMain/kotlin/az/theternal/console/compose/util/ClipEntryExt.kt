package az.theternal.console.compose.util

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry

actual fun String.toTextClipEntry(): ClipEntry = ClipEntry(ClipData.newPlainText("", this))
