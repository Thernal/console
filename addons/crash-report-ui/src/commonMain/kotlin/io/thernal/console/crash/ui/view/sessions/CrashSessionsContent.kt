package io.thernal.console.crash.ui.view.sessions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import io.thernal.console.api.ui.LocalLogRenderer
import io.thernal.console.crash.ui.session.CrashSessionClass
import io.thernal.console.crash.ui.session.CrashSessionSummary
import io.thernal.console.crash.ui.session.toDisplayLog
import io.thernal.console.crash.ui.view.sessions.model.CrashSessionsIntent
import io.thernal.console.crash.ui.view.sessions.model.CrashSessionsState
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.core.swipeaction.DsSwipeActionHost
import io.thernal.console.designsystem.components.core.swipeaction.DsSwipeActionPane
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.ui.core.preview

@Composable
internal fun CrashSessionsContent(
    state: CrashSessionsState,
    onSessionClick: (CrashSessionSummary) -> Unit,
    dispatch: (CrashSessionsIntent) -> Unit,
) {
    if (state.visibleSessions.value.isEmpty()) {
        CrashSessionsEmptyState()
    } else {
        SessionList(
            state = state,
            onSessionClick = onSessionClick,
            dispatch = dispatch,
        )
    }
}

@Composable
private fun SessionList(
    state: CrashSessionsState,
    onSessionClick: (CrashSessionSummary) -> Unit,
    dispatch: (CrashSessionsIntent) -> Unit,
) {
    val renderer = LocalLogRenderer.current

    DsSwipeActionHost {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = Theme.dimens.dp8,
                bottom = Theme.dimens.dp16,
                start = Theme.dimens.dp12,
                end = Theme.dimens.dp12,
            ),
            verticalArrangement = Arrangement.spacedBy(space = Theme.dimens.dp8),
        ) {
            items(
                items = state.visibleSessions.value,
                key = { it.id },
                contentType = { "crash_session_item" },
            ) { session ->
                DsSwipeActionPane(
                    itemId = session.id,
                    trailingWithProgress = { progress ->
                        DeleteSessionAction(
                            onClick = { dispatch(CrashSessionsIntent.Delete(session.id)) },
                            modifier = Modifier.graphicsLayer { alpha = progress },
                        )
                    },
                ) {
                    renderer.Item(
                        log = remember(session) { session.toDisplayLog() },
                        modifier = Modifier.pressable(
                            onPress = { onSessionClick(session) },
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteSessionAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DsIconButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxHeight()
            .padding(start = Theme.dimens.dp8),
    ) {
        DsIcon(
            icon = Icons.Outlined.Delete,
            color = Theme.colors.danger,
        )
    }
}

@Composable
private fun CrashSessionsEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DsIcon(
                icon = Icons.Outlined.ReportProblem,
                size = Theme.dimens.dp32,
                color = Theme.colors.content04,
            )
            DsText(
                text = "No crash sessions yet",
                style = Theme.typography.body02,
                color = Theme.colors.content03,
                modifier = Modifier.padding(top = Theme.dimens.dp8),
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewCrashSessionsContentEmpty() {
    ThemeProvider {
        CrashSessionsContent(
            state = CrashSessionsState(),
            onSessionClick = {},
            dispatch = {},
        )
    }
}

@DsPreview
@Composable
private fun PreviewCrashSessionsContentFilled() {
    val state = CrashSessionsState().preview {
        state.sessions.set(
            listOf(
                CrashSessionSummary(
                    id = "a",
                    startedAtMs = 1_700_000_000_000,
                    classification = CrashSessionClass.Confirmed,
                    summary = "IllegalStateException: boom",
                    crashedAtMs = 1_700_000_060_000,
                ),
                CrashSessionSummary(
                    id = "b",
                    startedAtMs = 1_699_990_000_000,
                    classification = CrashSessionClass.Probable,
                    summary = null,
                    crashedAtMs = null,
                ),
            ),
        )
    }
    ThemeProvider {
        CrashSessionsContent(
            state = state,
            onSessionClick = {},
            dispatch = {},
        )
    }
}
