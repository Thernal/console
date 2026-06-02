package io.thernal.console.stepper.compose.view.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.compose.core.preview
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.stepper.compose.view.settings.components.StepperActiveSection
import io.thernal.console.stepper.compose.view.settings.components.StepperCaughtGroup
import io.thernal.console.stepper.compose.view.settings.components.StepperPauseGroup
import io.thernal.console.stepper.compose.view.settings.components.StepperPausedGroup
import io.thernal.console.stepper.compose.view.settings.model.StepperIntent
import io.thernal.console.stepper.compose.view.settings.model.StepperSettingsState

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
