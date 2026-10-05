package io.thernal.console.stepper.ui.dock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.designsystem.components.core.chip.DsActionChip
import io.thernal.console.designsystem.components.core.chip.DsChipGroup
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.stepper.ui.navigation.StepperTab
import io.thernal.console.stepper.ui.stepper.Stepper
import io.thernal.console.stepper.ui.stepper.StepperIntent
import io.thernal.console.stepper.ui.view.overlay.components.OverlayCurrentLogSection
import io.thernal.console.stepper.ui.view.overlay.model.StepperOverlayViewModel

/** Every stepper control as a compact chip, then the event the pipeline is currently holding. */
@Composable
internal fun StepperPanel() {
    val state = viewModel { StepperOverlayViewModel() }.state
    val navigator = LocalConsoleNavigator.current
    val isEnabled = state.isEnabled.value
    val isPaused = state.isPaused.value

    Column(verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8)) {
        DsChipGroup(title = "Pipeline") {
            DsActionChip(
                label = if (isEnabled) "On" else "Off",
                icon = Icons.Outlined.PowerSettingsNew,
                isActive = isEnabled,
                onClick = { Stepper.dispatch(StepperIntent.ToggleEnabled) },
            )
            DsActionChip(
                label = if (isPaused) "Resume" else "Pause",
                icon = if (isPaused) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
                isActive = isPaused,
                isEnabled = isEnabled,
                onClick = { Stepper.dispatch(StepperIntent.TogglePaused) },
            )
            DsActionChip(
                label = "Step",
                icon = Icons.Outlined.SkipNext,
                isEnabled = isEnabled && state.canStep.value,
                onClick = { Stepper.dispatch(StepperIntent.Next) },
            )
            DsActionChip(
                label = "Tab",
                icon = Icons.Outlined.Settings,
                onClick = { navigator.openTab(StepperTab) },
            )
        }
        if (isEnabled) OverlayCurrentLogSection(currentLog = state.currentLog)
    }
}
