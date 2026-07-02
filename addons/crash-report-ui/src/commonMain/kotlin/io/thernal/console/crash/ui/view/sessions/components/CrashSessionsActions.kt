package io.thernal.console.crash.ui.view.sessions.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.thernal.console.crash.ui.CrashReports
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.foundation.theme.Theme

/**
 * App-bar action for the crash tab: clear-all with a two-tap destructive confirm — the first tap
 * arms (icon turns to the danger color), the second within the armed state deletes every past
 * session.
 */
@Composable
internal fun CrashSessionsActions() {
    val sessions by CrashReports.sessions.collectAsState()
    var isArmed by remember { mutableStateOf(false) }

    if (sessions.isEmpty()) {
        return
    }

    DsIconButton(
        onClick = {
            if (isArmed) {
                CrashReports.clearAll()
                isArmed = false
            } else {
                isArmed = true
            }
        },
    ) {
        DsIcon(
            icon = if (isArmed) Icons.Outlined.DeleteForever else Icons.Outlined.Delete,
            color = if (isArmed) Theme.colors.danger else Theme.colors.content02,
        )
    }
}
