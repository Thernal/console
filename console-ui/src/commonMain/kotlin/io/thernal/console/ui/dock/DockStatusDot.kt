package io.thernal.console.ui.dock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import io.thernal.console.api.addon.ConsoleDockStatus
import io.thernal.console.designsystem.foundation.theme.Theme

/** A small dot for a status other than idle; it pulses while the user must act. */
@Composable
internal fun DockStatusDot(
    status: ConsoleDockStatus,
    modifier: Modifier = Modifier,
) {
    if (status == ConsoleDockStatus.Idle) return
    Box(
        modifier
            .size(Theme.metrics.statusDotSize)
            .alpha(rememberPulse(isPulsing = status == ConsoleDockStatus.Attention))
            .background(status.tint(), CircleShape),
    )
}
