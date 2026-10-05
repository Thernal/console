package io.thernal.console.stepper.ui.dock

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.designsystem.components.core.DsQuickAction
import io.thernal.console.stepper.ui.stepper.Stepper
import io.thernal.console.stepper.ui.stepper.StepperIntent
import io.thernal.console.stepper.ui.view.overlay.model.StepperOverlayViewModel

/** Shown beside the collapsed dock while the pipeline is paused: step once, or resume. */
@Composable
internal fun StepperQuickAction() {
    val state = viewModel { StepperOverlayViewModel() }.state
    DsQuickAction(
        icon = Icons.Outlined.SkipNext,
        isEnabled = state.canStep.value,
        onClick = { Stepper.dispatch(StepperIntent.Next) },
    )
    DsQuickAction(
        icon = Icons.Outlined.PlayArrow,
        isPrimary = true,
        onClick = { Stepper.dispatch(StepperIntent.TogglePaused) },
    )
}
