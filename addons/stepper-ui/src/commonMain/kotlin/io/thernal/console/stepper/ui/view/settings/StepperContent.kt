package io.thernal.console.stepper.ui.view.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.ui.core.preview
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.stepper.ui.view.settings.components.StepperActiveSection
import io.thernal.console.stepper.ui.view.settings.components.StepperCaughtGroup
import io.thernal.console.stepper.ui.view.settings.components.StepperPauseGroup
import io.thernal.console.stepper.ui.view.settings.components.StepperPausedGroup
import io.thernal.console.stepper.ui.stepper.StepperIntent
import io.thernal.console.stepper.ui.view.settings.model.StepperSettingsIntent
import io.thernal.console.stepper.ui.view.settings.model.StepperSettingsState

@Composable
internal fun StepperSettingsContent(
    state: StepperSettingsState,
    dispatch: (StepperSettingsIntent) -> Unit,
    onStepperDispatch: (StepperIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Theme.dimens.dp16),
    ) {
        item {
            StepperActiveSection(
                checked = state.enabled,
                dispatch = onStepperDispatch,
            )
        }
        item {
            StepperPauseGroup(
                state = state,
                dispatch = onStepperDispatch,
            )
        }
        item {
            StepperPausedGroup(
                state = state,
                dispatch = dispatch,
                onStepperDispatch = onStepperDispatch,
            )
        }
        item {
            StepperCaughtGroup(
                state = state,
                dispatch = onStepperDispatch,
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewStepperSettingsContentDisabled() {
    ThemeProvider {
        StepperSettingsContent(
            state = StepperSettingsState(),
            dispatch = {},
            onStepperDispatch = {},
        )
    }
}

@DsPreview
@Composable
private fun PreviewStepperSettingsContentEnabled() {
    val state = StepperSettingsState().preview {
        state.enabled.set(true)
        state.paused.set(true)
        state.pauseOnMatch.set(true)
    }
    ThemeProvider {
        StepperSettingsContent(
            state = state,
            dispatch = {},
            onStepperDispatch = {},
        )
    }
}
