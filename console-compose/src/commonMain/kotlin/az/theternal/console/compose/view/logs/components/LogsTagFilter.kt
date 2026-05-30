package az.theternal.console.compose.view.logs.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.compose.core.preview
import az.theternal.console.compose.util.tagAccentColor
import az.theternal.console.compose.view.logs.model.LogsIntent
import az.theternal.console.compose.view.logs.model.LogsState
import az.theternal.console.designsystem.components.core.DsChip
import az.theternal.console.designsystem.components.modifier.pressable
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun LogsTagFilter(
    state: LogsState,
    dispatch: (LogsIntent) -> Unit,
) {
    val tags by state.tags
    val selectedTags by state.selectedTags

    if (tags.isEmpty()) return

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item {
            DsChip(
                label = "All Tags",
                color = Theme.colors.content02,
                selected = selectedTags.isEmpty(),
                modifier = Modifier.pressable(
                    onPress = { dispatch(LogsIntent.SelectAllTags) },
                ),
            )
        }

        items(
            items = tags,
            key = { it },
        ) { tag ->
            DsChip(
                label = tag,
                color = tagAccentColor(tag),
                selected = tag in selectedTags,
                modifier = Modifier.pressable(
                    onPress = { dispatch(LogsIntent.ToggleTag(tag)) },
                ),
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewLogsTagFilter() {
    ThemeProvider {
        val state = LogsState()
        state.tags.preview(listOf("API", "Auth", "Network"))
        state.selectedTags.preview(setOf("API"))
        LogsTagFilter(
            state = state,
            dispatch = {},
        )
    }
}
