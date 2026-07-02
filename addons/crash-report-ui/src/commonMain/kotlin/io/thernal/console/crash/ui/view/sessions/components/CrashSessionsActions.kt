package io.thernal.console.crash.ui.view.sessions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.crash.ui.CrashReports
import io.thernal.console.crash.ui.navigation.CrashReportSettingsRoute
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.foundation.theme.Theme

/**
 * App-bar actions for the crash tab: clear-all with a two-tap destructive confirm (the first tap
 * arms — icon turns to the danger color — the second within the armed state deletes every past
 * session), plus navigation to the crash report settings.
 */
@Composable
internal fun CrashSessionsActions() {
    val sessions by CrashReports.sessions.collectAsState()
    val navigator = LocalConsoleNavigator.current
    var isClearAllArmed by remember { mutableStateOf(false) }

    Row(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4)) {
        if (sessions.isNotEmpty()) {
            DsIconButton(
                onClick = {
                    if (isClearAllArmed) {
                        CrashReports.clearAll()
                        isClearAllArmed = false
                    } else {
                        isClearAllArmed = true
                    }
                },
            ) {
                DsIcon(
                    icon = if (isClearAllArmed) Icons.Outlined.DeleteForever else Icons.Outlined.Delete,
                    color = if (isClearAllArmed) Theme.colors.danger else Theme.colors.content02,
                )
            }
        }

        DsIconButton(
            onClick = { navigator.push(key = CrashReportSettingsRoute) },
        ) {
            DsIcon(
                icon = Icons.Outlined.Settings,
                color = Theme.colors.content02,
            )
        }
    }
}
