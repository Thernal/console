package io.thernal.console.compose.common

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry

@OptIn(ExperimentalComposeUiApi::class)
actual fun String.toTextClipEntry(): ClipEntry = ClipEntry.withPlainText(this)
