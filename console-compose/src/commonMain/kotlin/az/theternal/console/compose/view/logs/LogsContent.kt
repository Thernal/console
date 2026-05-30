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
import androidx.compose.ui.Modifier
import az.theternal.console.api.ui.LogRenderer
import az.theternal.console.compose.core.select
import az.theternal.console.compose.util.LocalSearchQuery
import az.theternal.console.compose.view.logs.components.LogsEmptyState
import az.theternal.console.compose.view.logs.components.LogsLevelFilter
import az.theternal.console.compose.view.logs.components.LogsSearchBar
import az.theternal.console.compose.view.logs.components.LogsTagFilter
import az.theternal.console.designsystem.components.core.collapsible.DsCollapsible
import az.theternal.console.designsystem.foundation.theme.Theme
import az.theternal.console.runtime.Log

@Composable
internal fun LogsContent(
    state: LogsState,
    renderer: LogRenderer,
    onLogClick: (Log) -> Unit,
    dispatch: (LogsIntent) -> Unit,
) {
    if (!state.hasAnyLogs.value) {
        LogsEmptyState()
        return
    }

    CompositionLocalProvider(LocalSearchQuery provides state.searchQuery.select { it.text }) {
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
                        state = state,
                        dispatch = dispatch,
                    )

                    LogsTagFilter(
                        state = state,
                        dispatch = dispatch,
                    )

                    LogsSearchBar(
                        state = state,
                        dispatch = dispatch,
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
                    items = state.logs.value,
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
