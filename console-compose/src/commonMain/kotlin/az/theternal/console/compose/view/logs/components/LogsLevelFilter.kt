package az.theternal.console.compose.view.logs.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.compose.util.themeColor
import az.theternal.console.compose.view.logs.model.LogsIntent
import az.theternal.console.compose.view.logs.model.LogsState
import az.theternal.console.designsystem.components.core.DsChip
import az.theternal.console.designsystem.components.modifier.pressable
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun LogsLevelFilter(
    state: LogsState,
    dispatch: (LogsIntent) -> Unit,
) {
    val selectedLevels by state.selectedLevels
    val allLevels by state.allLevels

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item {
            DsChip(
                label = "All Levels",
                color = Theme.colors.content02,
                selected = selectedLevels.isEmpty(),
                modifier = Modifier.pressable(onPress = { dispatch(LogsIntent.SelectAllLevels) }),
            )
        }

        items(items = allLevels, key = { it.name }) { level ->
            DsChip(
                label = level.name,
                color = level.themeColor(Theme.colors),
                selected = level in selectedLevels,
                modifier = Modifier.pressable(onPress = { dispatch(LogsIntent.ToggleLevel(level)) }),
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewLogsLevelFilter() {
    ThemeProvider {
        LogsLevelFilter(state = LogsState(), dispatch = {})
    }
}
