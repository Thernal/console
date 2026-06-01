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
internal fun StepperPauseGroup(
    state: StepperSettingsState,
    dispatch: (StepperIntent) -> Unit,
) {
    val isEnabled by state.enabled
    AnimatedVisibility(
        visible = isEnabled,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        Column {
            DsDivider()
            StepperPauseSection(
                checked = state.paused,
                dispatch = dispatch,
            )
        }
    }
}

@Composable
private fun StepperPauseSection(
    checked: State<Boolean>,
    dispatch: (StepperIntent) -> Unit,
) {
    SettingsToggleRow(
        label = "Pause",
        description = "Hold logs until you step through them",
        checked = checked.value,
        onCheckedChange = { dispatch(StepperIntent.SetPaused(it)) },
    )
}

@DsPreview
@Composable
private fun PreviewStepperPauseGroup() {
    val state = StepperSettingsState()
    state.enabled.preview(true)
    ThemeProvider {
        StepperPauseGroup(
            state = state,
            dispatch = {},
        )
    }
}
