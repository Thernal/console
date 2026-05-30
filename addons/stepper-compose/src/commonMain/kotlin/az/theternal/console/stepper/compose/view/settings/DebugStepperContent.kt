package az.theternal.console.stepper.compose.view.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import az.theternal.console.designsystem.components.core.DsDivider
import az.theternal.console.designsystem.foundation.theme.Theme
import az.theternal.console.stepper.compose.view.settings.components.StepperActiveSection
import az.theternal.console.stepper.compose.view.settings.components.StepperAutoResumeSection
import az.theternal.console.stepper.compose.view.settings.components.StepperCaughtSection
import az.theternal.console.stepper.compose.view.settings.components.StepperLevelSection
import az.theternal.console.stepper.compose.view.settings.components.StepperPauseOnMatchSection
import az.theternal.console.stepper.compose.view.settings.components.StepperPauseSection
import az.theternal.console.stepper.compose.view.settings.components.StepperTagsSection

@Composable
internal fun DebugStepperContent(
    state: DebugStepperSettingsState,
    dispatch: (DebugStepperIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Theme.dimens.dp16),
    ) {
        item { StepperActiveSection(config = state.config, dispatch = dispatch) }

        if (!state.config.value.enabled) return@LazyColumn

        item { DsDivider() }
        item { StepperPauseSection(config = state.config, dispatch = dispatch) }

        if (!state.config.value.paused) return@LazyColumn

        item { DsDivider() }
        item { StepperPauseOnMatchSection(config = state.config, dispatch = dispatch) }

        if (state.config.value.pauseOnMatch) {
            item { DsDivider() }
            item {
                StepperTagsSection(
                    config = state.config,
                    tagInput = state.tagInput,
                    dispatch = dispatch,
                )
            }
            item { DsDivider() }
            item { StepperLevelSection(config = state.config, dispatch = dispatch) }
        }

        item { DsDivider() }
        item { StepperAutoResumeSection(config = state.config, dispatch = dispatch) }

        if (state.stepperState.value.steppedEvents.isEmpty()) return@LazyColumn

        item { DsDivider() }
        item { StepperCaughtSection(stepperState = state.stepperState, dispatch = dispatch) }
    }
}
