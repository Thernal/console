package io.thernal.console.ui.logdetail.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalClipboard
import io.thernal.console.core.log.Log
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.ui.common.toTextClipEntry
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val COPIED_FEEDBACK_MS = 1_500L

/**
 * App-bar copy action for the shared log detail: copies the currently visible page's log via its
 * own [Log.toShareText], so custom types (e.g. network logs) contribute their full content.
 */
@Composable
internal fun CopyLogAction(activeLog: State<Log?>) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(COPIED_FEEDBACK_MS)
            isCopied = false
        }
    }

    val log = activeLog.value ?: return

    DsIconButton(
        onClick = {
            scope.launch {
                clipboard.setClipEntry(log.toShareText().toTextClipEntry())
            }
            isCopied = true
        },
    ) {
        DsIcon(
            icon = if (isCopied) Icons.Outlined.Check else Icons.Outlined.ContentCopy,
            color = if (isCopied) Theme.colors.success else Theme.colors.content02,
        )
    }
}
