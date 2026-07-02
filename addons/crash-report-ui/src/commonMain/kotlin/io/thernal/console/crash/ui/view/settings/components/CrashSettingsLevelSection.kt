package io.thernal.console.crash.ui.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.thernal.console.core.log.LogLevel
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.ui.core.select

@Composable
internal fun CrashSettingsLevelSection(
    selectedLevel: State<LogLevel?>,
    onLevelSelected: (LogLevel?) -> Unit,
) {
    CrashSettingsSection(title = "Minimum level") {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8)) {
            item {
                LevelChip(
                    label = "All",
                    isSelected = selectedLevel.select { it == null },
                    onPress = { onLevelSelected(null) },
                )
            }
            items(items = LogLevel.entries.filter { it != LogLevel.None }) { level ->
                LevelChip(
                    label = level.name,
                    isSelected = selectedLevel.select { it == level },
                    onPress = { onLevelSelected(level) },
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
private fun PreviewCrashSettingsLevelSection() {
    ThemeProvider {
        CrashSettingsLevelSection(
            selectedLevel = remember { mutableStateOf(null) },
            onLevelSelected = {},
        )
    }
}
