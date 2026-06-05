package io.thernal.console.compose.common

import androidx.compose.ui.platform.ClipEntry

expect fun String.toTextClipEntry(): ClipEntry
