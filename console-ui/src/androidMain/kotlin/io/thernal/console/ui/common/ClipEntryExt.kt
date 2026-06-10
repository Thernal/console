package io.thernal.console.ui.common

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry

actual fun String.toTextClipEntry(): ClipEntry = ClipEntry(ClipData.newPlainText("", this))
