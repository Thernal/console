package io.thernal.console.stepper.compose.view.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import io.thernal.console.compose.core.select
import io.thernal.console.designsystem.components.core.DsDivider
import io.thernal.console.stepper.compose.stepper.StepperIntent
import io.thernal.console.stepper.compose.view.settings.model.StepperSettingsState

@Composable
internal fun StepperCaughtGroup(
    state: StepperSettingsState,
    dispatch: (StepperIntent) -> Unit,
) {
    val hasEvents by state.steppedEvents.select { it.isNotEmpty() }

    AnimatedVisibility(
        visible = hasEvents,
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
