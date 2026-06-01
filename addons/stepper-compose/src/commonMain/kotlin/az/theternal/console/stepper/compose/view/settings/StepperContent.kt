package az.theternal.console.stepper.compose.view.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import az.theternal.console.compose.core.preview
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme
import az.theternal.console.stepper.compose.view.settings.components.StepperActiveSection
import az.theternal.console.stepper.compose.view.settings.components.StepperCaughtGroup
import az.theternal.console.stepper.compose.view.settings.components.StepperPauseGroup
import az.theternal.console.stepper.compose.view.settings.components.StepperPausedGroup
import az.theternal.console.stepper.compose.view.settings.model.StepperIntent
import az.theternal.console.stepper.compose.view.settings.model.StepperSettingsState

@Composable
internal fun StepperContent(
    state: StepperSettingsState,
    dispatch: (StepperIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Theme.dimens.dp16),
    ) {
        item {
            StepperActiveSection(
                checked = state.enabled,
                dispatch = dispatch,
            )
        }
        item {
            StepperPauseGroup(
                state = state,
                dispatch = dispatch,
            )
        }
        item {
            StepperPausedGroup(
                state = state,
                dispatch = dispatch,
            )
        }
        item {
            StepperCaughtGroup(
                state = state,
                dispatch = dispatch,
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewStepperContentDisabled() {
    ThemeProvider {
        StepperContent(
            state = StepperSettingsState(),
            dispatch = {},
        )
    }
}

@DsPreview
@Composable
private fun PreviewStepperContentEnabled() {
    val state = StepperSettingsState().preview {
        state.enabled.set(true)
        state.paused.set(true)
        state.isStepperActive.set(true)
        state.pauseOnMatch.set(true)
    }
    ThemeProvider {
        StepperContent(
            state = state,
            dispatch = {},
        )
    }
}
