package io.thernal.console.stepper.compose.view.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import io.thernal.console.compose.core.preview
import io.thernal.console.designsystem.components.core.DsDivider
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.stepper.compose.stepper.StepperIntent
import io.thernal.console.stepper.compose.view.settings.model.StepperSettingsIntent
import io.thernal.console.stepper.compose.view.settings.model.StepperSettingsState

@Composable
internal fun StepperPausedGroup(
    state: StepperSettingsState,
    dispatch: (StepperSettingsIntent) -> Unit,
    onStepperDispatch: (StepperIntent) -> Unit,
) {
    val isVisible by state.isStepperActive
    val isPauseOnMatch by state.pauseOnMatch

    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        Column {
            DsDivider()
            StepperPauseOnMatchSection(
                checked = state.pauseOnMatch,
                dispatch = onStepperDispatch,
            )
            AnimatedVisibility(
                visible = isPauseOnMatch,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column {
                    DsDivider()
                    StepperTagsSection(
                        pauseOnTags = state.pauseOnTags,
                        tagInput = state.tagInput,
                        dispatch = dispatch,
                        onStepperDispatch = onStepperDispatch,
                    )
                    DsDivider()
                    StepperLevelSection(
                        selectedLevel = state.pauseOnLevelAtLeast,
                        dispatch = onStepperDispatch,
                    )
                }
            }
            DsDivider()
            StepperAutoResumeSection(
                autoResumeSeconds = state.autoResumeSeconds,
                dispatch = onStepperDispatch,
            )
        }
    }
}

@Composable
private fun StepperPauseOnMatchSection(
    checked: State<Boolean>,
    dispatch: (StepperIntent) -> Unit,
) {
    SettingsToggleRow(
        label = "Pause on match",
        description = "Pause only when a log matches the filter",
        checked = checked.value,
        onCheckedChange = { dispatch(StepperIntent.SetPauseOnMatch(shouldPauseOnMatch = it)) },
    )
}

@DsPreview
@Composable
private fun PreviewStepperPausedGroup() {
    val state = StepperSettingsState().preview {
        state.enabled.set(true)
        state.paused.set(true)
        state.pauseOnMatch.set(true)
    }
    ThemeProvider {
        StepperPausedGroup(
            state = state,
            dispatch = {},
            onStepperDispatch = {},
        )
    }
}
