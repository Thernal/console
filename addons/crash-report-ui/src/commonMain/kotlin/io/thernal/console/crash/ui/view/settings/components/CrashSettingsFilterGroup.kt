package io.thernal.console.crash.ui.view.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import io.thernal.console.crash.ui.view.settings.model.CrashReportSettingsIntent
import io.thernal.console.crash.ui.view.settings.model.CrashReportSettingsState
import io.thernal.console.designsystem.components.core.DsDivider
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.ui.core.preview

/**
 * The save-filter block: the "persist on match" toggle with the level and tag criteria expanding
 * beneath it while the filter is active (mirrors the Stepper settings groups).
 */
@Composable
internal fun CrashSettingsFilterGroup(
    state: CrashReportSettingsState,
    dispatch: (CrashReportSettingsIntent) -> Unit,
) {
    val isPersistOnMatch by state.persistOnMatch

    Column {
        CrashSettingsToggleRow(
            label = "Filter persisted logs",
            description = "Only logs matching the criteria below are written to a session",
            checked = state.persistOnMatch,
            onCheckedChange = { dispatch(CrashReportSettingsIntent.SetPersistOnMatch(it)) },
        )
        AnimatedVisibility(
            visible = isPersistOnMatch,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column {
                DsDivider()
                CrashSettingsLevelSection(
                    selectedLevel = state.persistLevelAtLeast,
                    onLevelSelected = { dispatch(CrashReportSettingsIntent.SetPersistLevelAtLeast(it)) },
                )
                DsDivider()
                CrashSettingsTagsSection(
                    title = "Include tags",
                    emptyHint = "Matching all tags",
                    tags = state.includeTags,
                    tagInput = state.includeTagInput,
                    onTagInputChange = { dispatch(CrashReportSettingsIntent.SetIncludeTagInput(it)) },
                    onAddTag = { dispatch(CrashReportSettingsIntent.AddIncludeTag) },
                    onRemoveTag = { dispatch(CrashReportSettingsIntent.RemoveIncludeTag(it)) },
                )
                DsDivider()
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
    }
}

@DsPreview
@Composable
private fun PreviewCrashSettingsFilterGroup() {
    val state = CrashReportSettingsState().preview {
        state.persistOnMatch.set(true)
        state.includeTags.set(setOf("Network"))
    }
    ThemeProvider {
        CrashSettingsFilterGroup(
            state = state,
            dispatch = {},
        )
    }
}
