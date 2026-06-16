package io.thernal.console.logging.ui.view.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import io.thernal.console.api.ui.LogRenderer
import io.thernal.console.ui.common.LocalSearchQuery
import io.thernal.console.ui.core.preview
import io.thernal.console.ui.core.select
import io.thernal.console.logging.ui.addon.BasicLogRenderer
import io.thernal.console.logging.ui.view.logs.model.LogsIntent
import io.thernal.console.logging.ui.view.logs.model.LogsState
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.core.log.LogLevel
import io.thernal.console.logging.ui.view.logs.components.LogList
import io.thernal.console.logging.ui.view.logs.components.LogsEmptyState
import io.thernal.console.logging.ui.view.logs.components.LogsLevelFilter
import io.thernal.console.logging.ui.view.logs.components.LogsSearchBar
import io.thernal.console.logging.ui.view.logs.components.LogsTagFilter
import io.thernal.console.designsystem.components.core.collapsible.DsCollapsible
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.core.log.Log

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
            LogList(
                logs = state.logs,
                renderer = renderer,
                onLogClick = onLogClick,
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewLogsContentEmpty() {
    ThemeProvider {
        LogsContent(
            state = LogsState(),
            renderer = BasicLogRenderer(),
            onLogClick = {},
            dispatch = {},
        )
    }
}

@DsPreview
@Composable
private fun PreviewLogsContentFilled() {
    val state = LogsState().preview {
        state.hasAnyLogs.set(true)
        state.logs.set(
            listOf(
                Log(message = "User authenticated successfully", tag = "Auth", level = LogLevel.Info),
                Log(message = "Network request failed: 503", tag = "API", level = LogLevel.Error),
                Log(message = "Cache miss for key: user_profile", tag = "Cache", level = LogLevel.Debug),
            ),
        )
        state.tags.set(listOf("Auth", "API", "Cache"))
    }
    ThemeProvider {
        LogsContent(
            state = state,
            renderer = BasicLogRenderer(),
            onLogClick = {},
            dispatch = {},
        )
    }
}
