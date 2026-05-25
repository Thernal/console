package az.theternal.console.compose.view.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import az.theternal.console.api.ui.LogRenderer
import az.theternal.console.compose.util.LocalSearchQuery
import az.theternal.console.compose.view.logs.components.LogsEmptyState
import az.theternal.console.compose.view.logs.components.LogsLevelFilter
import az.theternal.console.compose.view.logs.components.LogsSearchBar
import az.theternal.console.compose.view.logs.components.LogsTagFilter
import az.theternal.console.designsystem.components.core.collapsible.DsCollapsible
import az.theternal.console.designsystem.foundation.theme.Theme
import az.theternal.console.runtime.Log
import az.theternal.console.runtime.LogLevel

@Composable
internal fun LogsContent(
    logs: List<Log>,
    hasAnyLogs: Boolean,
    allTags: List<String>,
    selectedTags: Set<String>,
    onTagToggle: (String) -> Unit,
    onTagSelectAll: () -> Unit,
    allLevels: List<LogLevel>,
    selectedLevels: Set<LogLevel>,
    onLevelToggle: (LogLevel) -> Unit,
    onLevelSelectAll: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onLogClick: (Log) -> Unit,
    renderer: LogRenderer,
) {
    if (!hasAnyLogs) {
        LogsEmptyState()
        return
    }

    val reversedLogs = remember(logs) { logs.asReversed() }

    CompositionLocalProvider(LocalSearchQuery provides searchQuery) {
        DsCollapsible(
            modifier = Modifier.fillMaxSize(),
            header = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = Theme.dimens.dp8,
                            start = Theme.dimens.dp12,
                            end = Theme.dimens.dp12,
                            bottom = Theme.dimens.dp6,
                        ),
                    verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
                ) {
                    LogsLevelFilter(
                        availableLevels = allLevels,
                        selectedLevels = selectedLevels,
                        onLevelToggle = onLevelToggle,
                        onSelectAll = onLevelSelectAll,
                    )
                    if (allTags.isNotEmpty()) {
                        LogsTagFilter(
                            tags = allTags,
                            selectedTags = selectedTags,
                            onTagToggle = onTagToggle,
                            onSelectAll = onTagSelectAll,
                        )
                    }
                    LogsSearchBar(
                        query = searchQuery,
                        onQueryChange = onSearchQueryChange,
                    )
                }
            },
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    top = Theme.dimens.dp8,
                    bottom = Theme.dimens.dp16,
                    start = Theme.dimens.dp12,
                    end = Theme.dimens.dp12,
                ),
                verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
            ) {
                items(
                    items = reversedLogs,
                    key = { it.id },
                    contentType = { "log_item" },
                ) { log ->
                    renderer.Item(
                        log = log,
                        onClick = { onLogClick(log) },
                    )
                }
            }
        }
    }
}
