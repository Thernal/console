package az.theternal.console.stepper.compose.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import az.theternal.console.compose.core.ViewState
import az.theternal.console.designsystem.components.core.DsChip
import az.theternal.console.designsystem.components.modifier.pressable
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme
import az.theternal.console.stepper.DebugStepper
import az.theternal.console.stepper.compose.view.settings.model.DebugStepperIntent
import az.theternal.console.stepper.compose.view.settings.model.DebugStepperSettingsState

private val autoResumeOptions = listOf(null, 3, 5, 10, 30)

@Composable
internal fun StepperAutoResumeSection(
    config: ViewState.StateField<DebugStepper.Config>,
    dispatch: (DebugStepperIntent) -> Unit,
) {
    SettingsSection(title = "Auto-resume") {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8)) {
            items(autoResumeOptions) { seconds ->
                DsChip(
                    label = if (seconds == null) "Off" else "${seconds}s",
                    selected = config.value.autoResumeSeconds == seconds,
                    modifier = Modifier.pressable(onPress = {
                        dispatch(DebugStepperIntent.SetAutoResumeSeconds(seconds))
                    }),
                )
            }
        }
    }
}

@DsPreview
@Composable
private fun PreviewStepperAutoResumeSection() {
    ThemeProvider {
        StepperAutoResumeSection(config = DebugStepperSettingsState().config, dispatch = {})
    }
}
