package io.thernal.console.logging.ui.view.logs.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboard
import io.thernal.console.ui.common.toTextClipEntry
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.logging.ui.ConsoleLogObserver
import io.thernal.console.logging.ui.view.logs.toShareText
import kotlinx.coroutines.launch

@Composable
internal fun LogsActions() {
    val logs by ConsoleLogObserver.logs.collectAsState()
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    if (logs.isEmpty()) {
        return
    }

    Row(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4)) {
        DsIconButton(
            onClick = {
                scope.launch {
                    clipboard.setClipEntry(logs.toShareText().toTextClipEntry())
                }
            },
        ) {
            DsIcon(
                icon = Icons.Outlined.ContentCopy,
                color = Theme.colors.content02,
            )
        }

        DsIconButton(onClick = ConsoleLogObserver::clear) {
            DsIcon(
                icon = Icons.Outlined.Delete,
                color = Theme.colors.content02,
            )
        }
    }
}
