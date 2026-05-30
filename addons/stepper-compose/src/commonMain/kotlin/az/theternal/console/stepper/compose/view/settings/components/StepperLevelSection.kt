package az.theternal.console.stepper.compose.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import az.theternal.console.compose.core.ViewState
import az.theternal.console.designsystem.components.core.DsChip
import az.theternal.console.designsystem.components.modifier.pressable
import az.theternal.console.designsystem.foundation.theme.Theme
import az.theternal.console.runtime.LogLevel
import az.theternal.console.stepper.DebugStepper
import az.theternal.console.stepper.compose.view.settings.DebugStepperIntent

@Composable
internal fun StepperLevelSection(
    config: ViewState.StateField<DebugStepper.Config>,
    dispatch: (DebugStepperIntent) -> Unit,
) {
    SettingsSection(title = "Level") {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8)) {
            item {
                DsChip(
                    label = "All",
                    color = Theme.colors.primary01,
                    selected = config.value.pauseOnLevelAtLeast == null,
                    modifier = Modifier.pressable(onPress = {
                        dispatch(DebugStepperIntent.SetPauseOnLevelAtLeast(null))
                    }),
                )
            }
            items(LogLevel.entries.filter { it != LogLevel.None }) { level ->
                DsChip(
                    label = level.name,
                    color = Theme.colors.primary01,
                    selected = config.value.pauseOnLevelAtLeast == level,
                    modifier = Modifier.pressable(onPress = {
                        dispatch(DebugStepperIntent.SetPauseOnLevelAtLeast(level))
                    }),
                )
            }
        }
    }
}
