package az.theternal.console.stepper.compose.view.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.stepper.compose.view.settings.model.StepperIntent

@Composable
internal fun StepperActiveSection(
    checked: State<Boolean>,
    dispatch: (StepperIntent) -> Unit,
) {
    SettingsToggleRow(
        label = "Active",
        description = "Enable or disable the stepper",
        checked = checked.value,
        onCheckedChange = { dispatch(StepperIntent.SetEnabled(it)) },
    )
}

@DsPreview
@Composable
private fun PreviewStepperActiveSection() {
    ThemeProvider {
        StepperActiveSection(
            checked = remember { mutableStateOf(false) },
            dispatch = {},
        )
    }
}
