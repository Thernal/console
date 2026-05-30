package az.theternal.console.stepper.compose.view.overlay.components

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
import az.theternal.console.stepper.compose.view.overlay.model.DebugStepperOverlayIntent
import az.theternal.console.stepper.compose.view.overlay.model.DebugStepperOverlayState
import az.theternal.console.stepper.compose.view.overlay.overlayBackgroundColor
import az.theternal.console.stepper.compose.view.overlay.overlayMutedContentColor
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun OverlayIconControls(
    state: DebugStepperOverlayState,
    dispatch: (DebugStepperOverlayIntent) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OverlayIconButton(
            onClick = { dispatch(DebugStepperOverlayIntent.ToggleEnabled) },
        ) {
            DsIcon(
                icon = Icons.Outlined.PowerSettingsNew,
                size = Theme.metrics.iconMd,
                color = if (state.isEnabled.value) Theme.colors.content01 else overlayMutedContentColor,
            )
        }

        if (state.isEnabled.value) {
            OverlayIconButton(
                onClick = { dispatch(DebugStepperOverlayIntent.TogglePaused) },
            ) {
                DsIcon(
                    icon = if (state.isPaused.value) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
                    size = Theme.metrics.iconMd,
                    color = Theme.colors.content01,
                )
            }

            OverlayIconButton(
                enabled = state.canStep.value,
                onClick = { dispatch(DebugStepperOverlayIntent.StepNext) },
            ) {
                DsIcon(
                    icon = Icons.Outlined.SkipNext,
                    size = Theme.metrics.iconMd,
                    color = if (state.canStep.value) Theme.colors.content01 else overlayMutedContentColor,
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
            val state = DebugStepperOverlayState()
            OverlayIconControls(
                state = state,
                dispatch = {},
            )
        }
    }
}
