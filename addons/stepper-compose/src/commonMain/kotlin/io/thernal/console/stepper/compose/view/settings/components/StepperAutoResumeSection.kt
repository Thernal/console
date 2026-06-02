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
import io.thernal.console.designsystem.components.core.DsChip
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.stepper.compose.view.settings.model.StepperIntent

private val autoResumeOptions = listOf(null, 3, 5, 10, 30)

@Composable
internal fun StepperAutoResumeSection(
    autoResumeSeconds: State<Int?>,
    dispatch: (StepperIntent) -> Unit,
) {
    SettingsSection(title = "Auto-resume") {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8)) {
            items(items = autoResumeOptions) { seconds ->
                AutoResumeChip(
                    seconds = seconds,
                    isSelected = autoResumeSeconds.select { it == seconds },
                    onPress = { dispatch(StepperIntent.SetAutoResumeSeconds(seconds)) },
                )
            }
        }
    }
}

@Composable
private fun AutoResumeChip(
    seconds: Int?,
    isSelected: State<Boolean>,
    onPress: () -> Unit,
) {
    DsChip(
        label = if (seconds == null) "Off" else "${seconds}s",
        selected = isSelected.value,
        modifier = Modifier.pressable(
            onPress = onPress,
        ),
    )
}

@DsPreview
@Composable
private fun PreviewStepperAutoResumeSection() {
    ThemeProvider {
        StepperAutoResumeSection(
            autoResumeSeconds = remember { mutableStateOf(null) },
            dispatch = {},
        )
    }
}
