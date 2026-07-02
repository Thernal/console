package io.thernal.console.crash.ui.view.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.crash.ui.view.settings.components.CrashSettingsBodyPolicySection
import io.thernal.console.crash.ui.view.settings.components.CrashSettingsLevelSection
import io.thernal.console.crash.ui.view.settings.components.CrashSettingsTagsSection
import io.thernal.console.crash.ui.view.settings.components.CrashSettingsToggleRow
import io.thernal.console.crash.ui.view.settings.model.CrashReportSettingsIntent
import io.thernal.console.crash.ui.view.settings.model.CrashReportSettingsState
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
internal fun CrashReportSettingsContent(
    state: CrashReportSettingsState,
    dispatch: (CrashReportSettingsIntent) -> Unit,
    onBack: () -> Unit,
) {
    DsScaffold(
        topBar = {
            DsAppBar(
                content = {
                    DsText(
                        text = "Crash report settings",
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
        SettingsBody(
            state = state,
            dispatch = dispatch,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
private fun SettingsBody(
    state: CrashReportSettingsState,
    dispatch: (CrashReportSettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Theme.dimens.dp16),
    ) {
        item {
            CrashSettingsToggleRow(
                label = "Show safe sessions",
                description = "List background and clean terminations too",
                checked = state.showSafeSessions.value,
                onCheckedChange = { dispatch(CrashReportSettingsIntent.SetShowSafeSessions(it)) },
            )
        }
        item {
            CrashSettingsToggleRow(
                label = "Filter persisted logs",
                description = "Only logs matching the criteria below are written to a session",
                checked = state.persistOnMatch.value,
                onCheckedChange = { dispatch(CrashReportSettingsIntent.SetPersistOnMatch(it)) },
            )
        }
        if (state.persistOnMatch.value) {
            item {
                CrashSettingsLevelSection(
                    selectedLevel = state.persistLevelAtLeast,
                    onLevelSelected = { dispatch(CrashReportSettingsIntent.SetPersistLevelAtLeast(it)) },
                )
            }
            item {
                CrashSettingsTagsSection(
                    title = "Include tags",
                    emptyHint = "Matching all tags",
                    tags = state.includeTags,
                    tagInput = state.includeTagInput,
                    onTagInputChange = { dispatch(CrashReportSettingsIntent.SetIncludeTagInput(it)) },
                    onAddTag = { dispatch(CrashReportSettingsIntent.AddIncludeTag) },
                    onRemoveTag = { dispatch(CrashReportSettingsIntent.RemoveIncludeTag(it)) },
                )
            }
            item {
                CrashSettingsTagsSection(
                    title = "Exclude tags",
                    emptyHint = "Nothing excluded",
                    tags = state.excludeTags,
                    tagInput = state.excludeTagInput,
                    onTagInputChange = { dispatch(CrashReportSettingsIntent.SetExcludeTagInput(it)) },
                    onAddTag = { dispatch(CrashReportSettingsIntent.AddExcludeTag) },
                    onRemoveTag = { dispatch(CrashReportSettingsIntent.RemoveExcludeTag(it)) },
                )
            }
        }
        item {
            CrashSettingsBodyPolicySection(
                selectedPolicy = state.bodyPolicy,
                onPolicySelected = { dispatch(CrashReportSettingsIntent.SetBodyPolicy(it)) },
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewCrashReportSettingsContent() {
    val state = CrashReportSettingsState().preview {
        state.persistOnMatch.set(true)
        state.includeTags.set(setOf("Network"))
    }
    ThemeProvider {
        CrashReportSettingsContent(
            state = state,
            dispatch = {},
            onBack = {},
        )
    }
}
