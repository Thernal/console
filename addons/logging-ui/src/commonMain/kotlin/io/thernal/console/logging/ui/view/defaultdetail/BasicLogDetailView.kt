package io.thernal.console.logging.ui.view.defaultdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboard
import io.thernal.console.ui.common.toTextClipEntry
import io.thernal.console.runtime.log.Log
import kotlinx.coroutines.launch

@Composable
internal fun BasicLogDetailView(log: Log) {
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()

    DefaultLogDetailContent(
        log = log,
        onCopy = {
            coroutineScope.launch {
                clipboard.setClipEntry(log.message.toTextClipEntry())
            }
        },
    )
}
