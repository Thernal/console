package io.thernal.console.stepper.ui.view.caught.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.stepper.ui.stepper.Stepper
import io.thernal.console.ui.core.select

/**
 * App-bar action for the Stepper tab: clear-all with a two-tap destructive confirm, mirroring
 * `CrashSessionsActions`. Scoped to `steppedEvents` alone (not the whole `Stepper.state`) since
 * that flow also carries `pendingLogs`, which changes on every captured log regardless of whether
 * anything is stepped — reading the whole object here would recompose this on every log event.
 */
@Composable
internal fun StepperCaughtActions() {
    val hasEvents by Stepper.state.collectAsState().select { it.steppedEvents.isNotEmpty() }
    var isClearAllArmed by remember { mutableStateOf(false) }

    if (!hasEvents) return

    DsIconButton(
        onClick = {
            if (isClearAllArmed) {
                Stepper.clearSteppedEvents()
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
