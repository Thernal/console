package az.theternal.console.compose.view.logs.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.compose.util.themeColor
import az.theternal.console.designsystem.components.core.DsChip
import az.theternal.console.designsystem.components.modifier.pressable
import az.theternal.console.designsystem.foundation.theme.Theme
import az.theternal.console.runtime.LogLevel

@Composable
internal fun LogsLevelFilter(
    availableLevels: List<LogLevel>,
    selectedLevels: Set<LogLevel>,
    onLevelToggle: (LogLevel) -> Unit,
    onSelectAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val allSelected = selectedLevels.isEmpty()

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsChip(
            label = "All Levels",
            color = Theme.colors.content02,
            selected = allSelected,
            modifier = Modifier.pressable(onPress = onSelectAll),
        )
        availableLevels.forEach { level ->
            DsChip(
                label = level.name,
                color = level.themeColor(Theme.colors),
                selected = level in selectedLevels,
                modifier = Modifier.pressable(onPress = { onLevelToggle(level) }),
            )
        }
    }
}
