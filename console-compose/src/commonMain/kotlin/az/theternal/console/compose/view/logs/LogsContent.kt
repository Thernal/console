package az.theternal.console.compose.view.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import az.theternal.console.api.ui.LogRenderer
import az.theternal.console.compose.core.preview
import az.theternal.console.compose.core.select
import az.theternal.console.compose.components.defaultLogRenderer
import az.theternal.console.compose.view.logs.model.LogsIntent
import az.theternal.console.compose.view.logs.model.LogsState
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.runtime.LogLevel
import az.theternal.console.compose.util.LocalSearchQuery
import az.theternal.console.compose.view.logs.components.LogList
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
            renderer = defaultLogRenderer(),
            onLogClick = {},
            dispatch = {},
        )
    }
}

@DsPreview
@Composable
private fun PreviewLogsContentFilled() {
    val state = LogsState()
    state.hasAnyLogs.preview(true)
    state.logs.preview(
        listOf(
            Log(message = "User authenticated successfully", tag = "Auth", level = LogLevel.Info),
            Log(message = "Network request failed: 503", tag = "API", level = LogLevel.Error),
            Log(message = "Cache miss for key: user_profile", tag = "Cache", level = LogLevel.Debug),
        ),
    )
    state.tags.preview(listOf("Auth", "API", "Cache"))
    ThemeProvider {
        LogsContent(
            state = state,
            renderer = defaultLogRenderer(),
            onLogClick = {},
            dispatch = {},
        )
    }
}
