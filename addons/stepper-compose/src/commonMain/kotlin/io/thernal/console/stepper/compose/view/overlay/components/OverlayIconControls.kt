package io.thernal.console.stepper.compose.view.overlay.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.stepper.compose.stepper.StepperIntent

@Composable
internal fun OverlayIconControls(
    isEnabled: State<Boolean>,
    isPaused: State<Boolean>,
    canStep: State<Boolean>,
    dispatch: (StepperIntent) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OverlayIconButton(onClick = { dispatch(StepperIntent.ToggleEnabled) }) {
            DsIcon(
                icon = Icons.Outlined.PowerSettingsNew,
                size = Theme.metrics.iconMd,
                color = if (isEnabled.value) Theme.colors.content01 else Theme.colors.content04,
            )
        }
        OverlayActiveControls(
            isEnabled = isEnabled,
            isPaused = isPaused,
            canStep = canStep,
            dispatch = dispatch,
        )
    }
}

@Composable
private fun OverlayActiveControls(
    isEnabled: State<Boolean>,
    isPaused: State<Boolean>,
    canStep: State<Boolean>,
    dispatch: (StepperIntent) -> Unit,
) {
    if (!isEnabled.value) return
    OverlayIconButton(onClick = { dispatch(StepperIntent.TogglePaused) }) {
        DsIcon(
            icon = if (isPaused.value) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
            size = Theme.metrics.iconMd,
            color = Theme.colors.content01,
        )
    }
    OverlayStepButton(canStep = canStep, dispatch = dispatch)
}

@Composable
private fun OverlayStepButton(
    canStep: State<Boolean>,
    dispatch: (StepperIntent) -> Unit,
) {
    OverlayIconButton(
        enabled = canStep.value,
        onClick = { dispatch(StepperIntent.Next) },
    ) {
        DsIcon(
            icon = Icons.Outlined.SkipNext,
            size = Theme.metrics.iconMd,
            color = if (canStep.value) Theme.colors.content01 else Theme.colors.content04,
        )
    }
}
