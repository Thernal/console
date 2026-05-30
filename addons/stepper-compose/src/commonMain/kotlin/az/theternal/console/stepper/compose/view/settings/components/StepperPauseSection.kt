package az.theternal.console.stepper.compose.view.settings.components

import androidx.compose.runtime.Composable
import az.theternal.console.compose.core.ViewState
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.stepper.DebugStepper
import az.theternal.console.stepper.compose.view.settings.model.DebugStepperIntent
import az.theternal.console.stepper.compose.view.settings.model.DebugStepperSettingsState

@Composable
internal fun StepperPauseSection(
    config: ViewState.StateField<DebugStepper.Config>,
    dispatch: (DebugStepperIntent) -> Unit,
) {
    SettingsToggleRow(
        label = "Pause",
        description = "Hold logs until you step through them",
        checked = config.value.paused,
        onCheckedChange = { dispatch(DebugStepperIntent.SetPaused(it)) },
    )
}

@DsPreview
@Composable
private fun PreviewStepperPauseSection() {
    ThemeProvider {
        StepperPauseSection(config = DebugStepperSettingsState().config, dispatch = {})
    }
}
