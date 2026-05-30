package az.theternal.console.stepper.compose.view.settings.components

import androidx.compose.runtime.Composable
import az.theternal.console.compose.core.ViewState
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.stepper.DebugStepper
import az.theternal.console.stepper.compose.view.settings.model.DebugStepperIntent
import az.theternal.console.stepper.compose.view.settings.model.DebugStepperSettingsState

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

@DsPreview
@Composable
private fun PreviewStepperActiveSection() {
    ThemeProvider {
        StepperActiveSection(config = DebugStepperSettingsState().config, dispatch = {})
    }
}
