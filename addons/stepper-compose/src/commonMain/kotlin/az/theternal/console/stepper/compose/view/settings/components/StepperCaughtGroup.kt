package az.theternal.console.stepper.compose.view.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import az.theternal.console.compose.core.select
import az.theternal.console.designsystem.components.core.DsDivider
import az.theternal.console.stepper.compose.view.settings.model.StepperIntent
import az.theternal.console.stepper.compose.view.settings.model.StepperSettingsState

@Composable
internal fun StepperCaughtGroup(
    state: StepperSettingsState,
    dispatch: (StepperIntent) -> Unit,
) {
    val isActive by state.isStepperActive
    val hasEvents by state.steppedEvents.select { it.isNotEmpty() }

    AnimatedVisibility(
        visible = isActive && hasEvents,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        Column {
            DsDivider()
            StepperCaughtSection(
                events = state.steppedEvents,
                dispatch = dispatch,
            )
        }
    }
}
