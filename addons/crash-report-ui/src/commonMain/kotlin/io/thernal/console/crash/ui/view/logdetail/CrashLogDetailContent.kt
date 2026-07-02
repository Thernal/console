package io.thernal.console.crash.ui.view.logdetail

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.api.ui.LocalLogRenderer
import io.thernal.console.crash.ui.view.logdetail.model.CrashLogDetailState
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
internal fun CrashLogDetailContent(
    state: CrashLogDetailState,
    onBack: () -> Unit,
) {
    DsScaffold(
        topBar = {
            DsAppBar(
                content = {
                    DsText(
                        text = state.log.value?.tab ?: "Log",
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
            )
        },
    ) { padding ->
        LogDetailBody(
            state = state,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
private fun LogDetailBody(
    state: CrashLogDetailState,
    modifier: Modifier = Modifier,
) {
    val renderer = LocalLogRenderer.current
    val log = state.log.value

    if (log != null) {
        // Same container contract as logging-ui's detail pager: the renderer's Detail owns
        // neither padding nor scrolling, the host screen provides both.
        val scrollState = remember(log.id) { ScrollState(initial = 0) }
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    horizontal = Theme.dimens.dp12,
                    vertical = Theme.dimens.dp16,
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
            ) {
                renderer.Detail(log = log)
            }
        }
    } else if (state.isLoaded.value) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            DsText(
                text = "Log not found",
                style = Theme.typography.body02,
                color = Theme.colors.content03,
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewCrashLogDetailContent() {
    val state = CrashLogDetailState().preview {
        state.log.set(
            Log(
                message = "IllegalStateException: boom\n  at PlaygroundViewModel.crash(...)",
                tag = "Crash",
                level = LogLevel.Fatal,
            ),
        )
        state.isLoaded.set(true)
    }
    ThemeProvider {
        CrashLogDetailContent(
            state = state,
            onBack = {},
        )
    }
}
