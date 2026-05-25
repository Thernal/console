package az.theternal.console.compose.view.logs.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.compose.util.tagAccentColor
import az.theternal.console.designsystem.components.core.DsChip
import az.theternal.console.designsystem.components.modifier.pressable
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun LogsTagFilter(
    tags: List<String>,
    selectedTags: Set<String>,
    onTagToggle: (String) -> Unit,
    onSelectAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val allSelected = selectedTags.isEmpty()

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsChip(
            label = "All Tags",
            color = Theme.colors.content02,
            selected = allSelected,
            modifier = Modifier.pressable(onPress = onSelectAll),
        )
        tags.forEach { tag ->
            DsChip(
                label = tag,
                color = tagAccentColor(tag),
                selected = tag in selectedTags,
                modifier = Modifier.pressable(onPress = { onTagToggle(tag) }),
            )
        }
    }
}
