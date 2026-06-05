package io.thernal.console.compose.common

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalComposeUiApi::class)
actual fun String.toTextClipEntry(): ClipEntry = ClipEntry(StringSelection(this))
