package io.thernal.console.crash.ui.view.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import io.thernal.console.api.ui.LocalLogRenderer
import io.thernal.console.crash.ui.view.detail.model.CrashSessionDetailState
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import io.thernal.console.designsystem.components.core.DsAppBar
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.components.core.DsScaffold
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.ui.core.preview

@Composable
internal fun CrashSessionDetailContent(
    state: CrashSessionDetailState,
    onLogClick: (Log) -> Unit,
    onBack: () -> Unit,
    onShareClick: () -> Unit,
) {
    DsScaffold(
        topBar = {
            DsAppBar(
                content = { CrashSessionDetailTitle(logs = state.logs) },
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
            onLogClick = onLogClick,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
private fun CrashSessionDetailTitle(logs: State<List<Log>>) {
    DsText(
        text = "Session (${logs.value.size})",
        style = Theme.typography.title01,
        color = Theme.colors.content01,
    )
}

@Composable
private fun SessionLogList(
    state: CrashSessionDetailState,
    onLogClick: (Log) -> Unit,
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
            items = state.logs.value,
            key = { it.id },
            contentType = { "log_item" },
        ) { log ->
            renderer.Item(
                log = log,
                modifier = Modifier.pressable(
                    onPress = { onLogClick(log) },
                ),
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewCrashSessionDetailContent() {
    val state = CrashSessionDetailState().preview {
        state.logs.set(
            listOf(
                Log(
                    message = "IllegalStateException: boom\n  at PlaygroundViewModel.crash(...)",
                    tag = "Crash",
                    level = LogLevel.Fatal,
                ),
                Log(message = "Cart sync failed", tag = "Cart", level = LogLevel.Warning),
                Log(message = "Auth token refreshed", tag = "Auth", level = LogLevel.Info),
            ),
        )
        state.isLoaded.set(true)
    }
    ThemeProvider {
        CrashSessionDetailContent(
            state = state,
            onLogClick = {},
            onBack = {},
            onShareClick = {},
        )
    }
}
