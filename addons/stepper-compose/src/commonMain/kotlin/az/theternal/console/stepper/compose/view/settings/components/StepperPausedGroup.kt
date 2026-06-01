package az.theternal.console.stepper.compose.view.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import az.theternal.console.compose.core.preview
import az.theternal.console.designsystem.components.core.DsDivider
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.stepper.compose.view.settings.model.StepperIntent
import az.theternal.console.stepper.compose.view.settings.model.StepperSettingsState

@Composable
internal fun StepperPausedGroup(
    state: StepperSettingsState,
    dispatch: (StepperIntent) -> Unit,
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
                dispatch = dispatch,
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
                    )
                    DsDivider()
                    StepperLevelSection(
                        selectedLevel = state.pauseOnLevelAtLeast,
                        dispatch = dispatch,
                    )
                }
            }
            DsDivider()
            StepperAutoResumeSection(
                autoResumeSeconds = state.autoResumeSeconds,
                dispatch = dispatch,
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
        onCheckedChange = { dispatch(StepperIntent.SetPauseOnMatch(it)) },
    )
}

@DsPreview
@Composable
private fun PreviewStepperPausedGroup() {
    val state = StepperSettingsState()
    state.isStepperActive.preview(true)
    state.pauseOnMatch.preview(true)
    ThemeProvider {
        StepperPausedGroup(
            state = state,
            dispatch = {},
        )
    }
}
