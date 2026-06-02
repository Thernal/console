package io.thernal.console.compose.view.logs.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.compose.core.select
import io.thernal.console.compose.util.themeColor
import io.thernal.console.compose.view.logs.model.LogsIntent
import io.thernal.console.compose.view.logs.model.LogsState
import io.thernal.console.designsystem.components.core.DsChip
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.runtime.LogLevel

@Composable
internal fun LogsLevelFilter(
    state: LogsState,
    dispatch: (LogsIntent) -> Unit,
) {
    val allLevels = remember { LogLevel.entries.filter { it != LogLevel.None } }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item {
            AllLevelsButton(
                isSelected = state.selectedLevels.select { it.isEmpty() },
                dispatch = dispatch,
            )
        }

        items(
            items = allLevels,
            key = { it.name },
        ) { level ->
            Level(
                level = level,
                dispatch = dispatch,
                isSelected = state.selectedLevels.select { level in it },
            )
        }
    }
}

@Composable
private fun AllLevelsButton(
    isSelected: State<Boolean>,
    dispatch: (LogsIntent) -> Unit,
) {
    DsChip(
        label = "All Levels",
        color = Theme.colors.content02,
        selected = isSelected.value,
        modifier = Modifier.pressable(
            onPress = { dispatch(LogsIntent.SelectAllLevels) },
        ),
    )
}

@Composable
private fun Level(
    level: LogLevel,
    isSelected: State<Boolean>,
    dispatch: (LogsIntent) -> Unit,
) {
    DsChip(
        label = level.name,
        color = level.themeColor(Theme.colors),
        selected = isSelected.value,
        modifier = Modifier.pressable(
            onPress = { dispatch(LogsIntent.ToggleLevel(level)) },
        ),
    )
}

@DsPreview
@Composable
private fun PreviewLogsLevelFilter() {
    ThemeProvider {
        LogsLevelFilter(state = LogsState(), dispatch = {})
    }
}
