package io.thernal.console.logging.ui.view.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.api.ui.LogRenderer
import io.thernal.console.ui.common.LocalSearchQuery
import io.thernal.console.ui.core.preview
import io.thernal.console.ui.core.select
import io.thernal.console.logging.ui.addon.BasicLogRenderer
import io.thernal.console.logging.ui.view.detail.components.LogDetailPager
import io.thernal.console.logging.ui.view.detail.components.LogNotFoundState
import io.thernal.console.logging.ui.view.detail.components.LogDetailSearchField
import io.thernal.console.logging.ui.view.detail.components.LogDetailTabs
import io.thernal.console.logging.ui.view.detail.components.LogMeta
import io.thernal.console.logging.ui.view.detail.model.LogDetailIntent
import io.thernal.console.logging.ui.view.detail.model.LogDetailState
import io.thernal.console.logging.ui.view.detail.model.rememberLogDetailTabState
import io.thernal.console.designsystem.components.core.DsAppBar
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.components.core.DsScaffold
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.core.collapsible.DsCollapsible
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.runtime.log.Log
import io.thernal.console.runtime.log.LogLevel

@Composable
internal fun LogDetailContent(
    state: LogDetailState,
    onDispatch: (LogDetailIntent) -> Unit,
    onBack: () -> Unit,
    renderer: LogRenderer,
) {
    val tabState = rememberLogDetailTabState(state = state)

    if (tabState == null) {
        LogNotFoundState()
        return
    }

    DsScaffold(
        topBar = {
            DsAppBar(
                leading = {
                    DsIconButton(onClick = onBack) {
                        DsIcon(
                            icon = Icons.AutoMirrored.Filled.ArrowBackIos,
                            color = Theme.colors.content02,
                        )
                    }
                    DsText(
                        text = "Log Detail",
                        style = Theme.typography.title01,
                        color = Theme.colors.content01,
                    )
                },
            )
        },
    ) { padding ->
        CompositionLocalProvider(
            LocalSearchQuery provides state.searchQuery.select { it.text },
        ) {
            val verticalArrangement = Arrangement.spacedBy(
                space = Theme.dimens.dp12,
            )

            DsCollapsible(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(
                        horizontal = Theme.dimens.dp12,
                        vertical = Theme.dimens.dp16,
                    ),
                verticalArrangement = verticalArrangement,
                header = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = verticalArrangement,
                    ) {
                        if (state.showTabs.value) {
                            LogDetailTabs(
                                tabState = tabState,
                                logs = state.logs,
                            )
                        }

                        state.activeLog.value?.let { log ->
                            LogMeta(log = log)
                        }

                        if (state.showSearch.value) {
                            LogDetailSearchField(
                                searchQuery = state.searchQuery,
                                onChange = { onDispatch(LogDetailIntent.SetQuery(query = it)) },
                            )
                        }
                    }
                },
            ) {
                LogDetailPager(
                    pagerState = tabState.pagerState,
                    logs = state.logs,
                    renderer = renderer,
                )
            }
        }
    }
}

@DsPreview
@Composable
private fun PreviewLogDetailContentFound() {
    ThemeProvider {
        LogDetailContent(
            state = LogDetailState().preview {
                state.isInitialized.set(true)
                state.logs.set(
                    listOf(
                        Log(
                            message = "Network request completed in 342ms",
                            tag = "HTTP",
                            level = LogLevel.Info,
                        ),
                    ),
                )
            },
            onDispatch = {},
            onBack = {},
            renderer = BasicLogRenderer(),
        )
    }
}
