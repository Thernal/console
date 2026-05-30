package az.theternal.console.stepper.compose.view.settings.components

import androidx.compose.runtime.Composable
import az.theternal.console.compose.core.ViewState
import az.theternal.console.stepper.DebugStepper
import az.theternal.console.stepper.compose.view.settings.DebugStepperIntent

@Composable
internal fun StepperPauseOnMatchSection(
    config: ViewState.StateField<DebugStepper.Config>,
    dispatch: (DebugStepperIntent) -> Unit,
) {
    SettingsToggleRow(
        label = "Pause on match",
        description = "Pause only when a log matches the filter",
        checked = config.value.pauseOnMatch,
        onCheckedChange = { dispatch(DebugStepperIntent.SetPauseOnMatch(it)) },
    )
}
