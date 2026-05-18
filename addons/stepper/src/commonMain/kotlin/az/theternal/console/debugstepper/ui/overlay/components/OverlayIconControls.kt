package az.theternal.console.debugstepper.ui.overlay.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.debugstepper.api.DebugStepper
import az.theternal.console.debugstepper.ui.overlay.overlayBackgroundColor
import az.theternal.console.debugstepper.ui.overlay.overlayMutedContentColor
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun OverlayIconControls(
    isEnabled: Boolean,
    isPaused: Boolean,
    canStep: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OverlayIconButton(
            onClick = { DebugStepper.updateConfig { copy(enabled = !isEnabled) } },
        ) {
            DsIcon(
                icon = Icons.Outlined.PowerSettingsNew,
                size = Theme.metrics.iconMd,
                color = if (isEnabled) Theme.colors.content01 else overlayMutedContentColor,
            )
        }

        if (isEnabled) {
            OverlayIconButton(
                onClick = { DebugStepper.updateConfig { copy(paused = !isPaused) } },
            ) {
                DsIcon(
                    icon = if (isPaused) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
                    size = Theme.metrics.iconMd,
                    color = Theme.colors.content01,
                )
            }

            OverlayIconButton(
                enabled = canStep,
                onClick = { DebugStepper.next() },
            ) {
                DsIcon(
                    icon = Icons.Outlined.SkipNext,
                    size = Theme.metrics.iconMd,
                    color = if (canStep) Theme.colors.content01 else overlayMutedContentColor,
                )
            }
        }
    }
}

@DsPreview
@Composable
private fun PreviewOverlayControls() {
    ThemeProvider {
        Column(
            modifier = Modifier
                .background(overlayBackgroundColor)
                .padding(Theme.dimens.dp8),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
        ) {
            OverlayIconControls(isEnabled = true, isPaused = true, canStep = true)
            OverlayIconControls(isEnabled = true, isPaused = false, canStep = false)
            OverlayIconControls(isEnabled = false, isPaused = false, canStep = false)
        }
    }
}
