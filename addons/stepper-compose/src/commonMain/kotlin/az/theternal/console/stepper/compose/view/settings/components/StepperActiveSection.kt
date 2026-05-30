package az.theternal.console.stepper.compose.view.settings.components

import androidx.compose.runtime.Composable
import az.theternal.console.compose.core.ViewState
import az.theternal.console.stepper.DebugStepper
import az.theternal.console.stepper.compose.view.settings.DebugStepperIntent

@Composable
internal fun StepperActiveSection(
    config: ViewState.StateField<DebugStepper.Config>,
    dispatch: (DebugStepperIntent) -> Unit,
) {
    SettingsToggleRow(
        label = "Active",
        description = "Enable or disable the debug stepper",
        checked = config.value.enabled,
        onCheckedChange = { dispatch(DebugStepperIntent.SetEnabled(it)) },
    )
}
