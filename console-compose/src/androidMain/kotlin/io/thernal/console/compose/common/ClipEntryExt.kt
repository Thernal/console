package io.thernal.console.compose.common

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry

actual fun String.toTextClipEntry(): ClipEntry = ClipEntry(ClipData.newPlainText("", this))
