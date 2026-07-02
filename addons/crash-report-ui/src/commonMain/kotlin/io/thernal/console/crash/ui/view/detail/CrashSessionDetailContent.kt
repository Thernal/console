package io.thernal.console.crash.ui.view.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.api.ui.LocalLogRenderer
import io.thernal.console.crash.ui.view.detail.components.FatalCrashCard
import io.thernal.console.crash.ui.view.detail.model.CrashSessionDetailState
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import io.thernal.console.designsystem.components.core.DsAppBar
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.components.core.DsScaffold
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.ui.core.preview

@Composable
internal fun CrashSessionDetailContent(
    state: CrashSessionDetailState,
    onBack: () -> Unit,
    onShareClick: () -> Unit,
) {
    DsScaffold(
        topBar = {
            DsAppBar(
                content = {
                    DsText(
                        text = state.crashSummary.value ?: "Session logs",
                        style = Theme.typography.title01,
                        color = Theme.colors.content01,
                    )
                },
                leading = {
                    DsIconButton(onClick = onBack) {
                        DsIcon(
                            icon = Icons.AutoMirrored.Filled.ArrowBackIos,
                            color = Theme.colors.content02,
                        )
                    }
                },
                trailing = {
                    DsIconButton(onClick = onShareClick) {
                        DsIcon(
                            icon = Icons.Outlined.ContentCopy,
                            color = Theme.colors.content02,
                        )
                    }
                },
            )
        },
    ) { padding ->
        SessionLogList(
            state = state,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
private fun SessionLogList(
    state: CrashSessionDetailState,
    modifier: Modifier = Modifier,
) {
    val renderer = LocalLogRenderer.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = Theme.dimens.dp8,
            bottom = Theme.dimens.dp16,
            start = Theme.dimens.dp12,
            end = Theme.dimens.dp12,
        ),
        verticalArrangement = Arrangement.spacedBy(space = Theme.dimens.dp8),
    ) {
        items(
            count = state.precedingLogs.value.size,
            key = { index -> state.precedingLogs.value[index].id },
            contentType = { "log_item" },
        ) { index ->
            renderer.Item(
                log = state.precedingLogs.value[index],
                modifier = Modifier,
            )
        }

        val stackTrace = state.stackTrace.value
        if (stackTrace != null) {
            item(key = "fatal_crash", contentType = "fatal_crash") {
                FatalCrashCard(
                    summary = state.crashSummary.value ?: "Fatal crash",
                    stackTrace = stackTrace,
                )
            }
        }
    }
}

@DsPreview
@Composable
private fun PreviewCrashSessionDetailContent() {
    val state = CrashSessionDetailState().preview {
        state.precedingLogs.set(
            listOf(
                Log(message = "Auth token refreshed", tag = "Auth", level = LogLevel.Info),
                Log(message = "Cart sync failed", tag = "Cart", level = LogLevel.Warning),
            ),
        )
        state.crashSummary.set("IllegalStateException: boom")
        state.stackTrace.set("IllegalStateException: boom\n  at PlaygroundViewModel.crash(...)")
        state.isLoaded.set(true)
    }
    ThemeProvider {
        CrashSessionDetailContent(
            state = state,
            onBack = {},
            onShareClick = {},
        )
    }
}
