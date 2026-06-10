package io.thernal.console.ui.common

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalComposeUiApi::class)
actual fun String.toTextClipEntry(): ClipEntry = ClipEntry(StringSelection(this))
