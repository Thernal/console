package io.thernal.console.crash.ui.view.sessions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.crash.ui.session.CrashSessionClass
import io.thernal.console.crash.ui.session.CrashSessionSummary
import io.thernal.console.crash.ui.view.sessions.components.CrashSessionItem
import io.thernal.console.crash.ui.view.sessions.model.CrashSessionsIntent
import io.thernal.console.crash.ui.view.sessions.model.CrashSessionsState
import io.thernal.console.designsystem.components.core.DsSwitch
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsText
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
    Column(modifier = Modifier.fillMaxSize()) {
        SafeToggleRow(
            state = state,
            dispatch = dispatch,
        )

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
}

@Composable
private fun SafeToggleRow(
    state: CrashSessionsState,
    dispatch: (CrashSessionsIntent) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Theme.dimens.dp12,
                vertical = Theme.dimens.dp6,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = "Show safe terminations (${state.hiddenSafeCount.value})",
            style = Theme.typography.body02,
            color = Theme.colors.content02,
        )

        DsSwitch(
            checked = state.showSafe.value,
            onCheckedChange = { dispatch(CrashSessionsIntent.SetShowSafe(it)) },
        )
    }
}

@Composable
private fun SessionList(
    state: CrashSessionsState,
    onSessionClick: (CrashSessionSummary) -> Unit,
    dispatch: (CrashSessionsIntent) -> Unit,
) {
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
            val isDeleteArmed = state.armedDeleteId.value == session.id
            CrashSessionItem(
                session = session,
                isDeleteArmed = isDeleteArmed,
                onDeleteClick = {
                    val intent = if (isDeleteArmed) {
                        CrashSessionsIntent.ConfirmDelete(session.id)
                    } else {
                        CrashSessionsIntent.ArmDelete(session.id)
                    }
                    dispatch(intent)
                },
                modifier = Modifier.pressable(
                    onPress = { onSessionClick(session) },
                ),
            )
        }
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
