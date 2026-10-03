package io.thernal.console.ui.dock

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.thernal.console.api.addon.ConsoleDockStatus
import io.thernal.console.designsystem.foundation.theme.Theme

/** Grey while idle, the brand colour while running, amber when the user must act. */
@Composable
internal fun ConsoleDockStatus.tint(): Color {
    return when (this) {
        ConsoleDockStatus.Idle -> Theme.colors.content03
        ConsoleDockStatus.Active -> Theme.colors.primary01
        ConsoleDockStatus.Attention -> Theme.colors.warning
    }
}
