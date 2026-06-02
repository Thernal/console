package io.thernal.console.compose.view.logs.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.compose.core.preview
import io.thernal.console.compose.util.tagAccentColor
import io.thernal.console.compose.view.logs.model.LogsIntent
import io.thernal.console.compose.view.logs.model.LogsState
import io.thernal.console.designsystem.components.core.DsChip
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

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
        val state = LogsState().preview {
            state.tags.set(listOf("API", "Auth", "Network"))
            state.selectedTags.set(setOf("API"))
        }
        LogsTagFilter(
            state = state,
            dispatch = {},
        )
    }
}
