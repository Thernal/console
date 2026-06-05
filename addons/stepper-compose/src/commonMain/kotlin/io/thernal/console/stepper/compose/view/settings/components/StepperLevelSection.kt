package io.thernal.console.stepper.compose.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.thernal.console.compose.core.select
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.runtime.log.LogLevel
import io.thernal.console.stepper.compose.stepper.StepperIntent

@Composable
internal fun StepperLevelSection(
    selectedLevel: State<LogLevel?>,
    dispatch: (StepperIntent) -> Unit,
) {
    SettingsSection(title = "Level") {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8)) {
            item {
                LevelChip(
                    label = "All",
                    isSelected = selectedLevel.select { it == null },
                    onPress = { dispatch(StepperIntent.SetPauseOnLevelAtLeast(level = null)) },
                )
            }
            items(items = LogLevel.entries.filter { it != LogLevel.None }) { level ->
                LevelChip(
                    label = level.name,
                    isSelected = selectedLevel.select { it == level },
                    onPress = { dispatch(StepperIntent.SetPauseOnLevelAtLeast(level = level)) },
                )
            }
        }
    }
}

@Composable
private fun LevelChip(
    label: String,
    isSelected: State<Boolean>,
    onPress: () -> Unit,
) {
    DsChip(
        label = label,
        color = Theme.colors.primary01,
        selected = isSelected.value,
        modifier = Modifier.pressable(
            onPress = onPress,
        ),
    )
}

@DsPreview
@Composable
private fun PreviewStepperLevelSection() {
    ThemeProvider {
        StepperLevelSection(
            selectedLevel = remember { mutableStateOf(null) },
            dispatch = {},
        )
    }
}
