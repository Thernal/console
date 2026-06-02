package io.thernal.console.stepper.compose.view.events

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.stepper.compose.view.events.model.SteppedEventsViewModel

@Composable
internal fun SteppedEventsView() {
    val viewModel = viewModel { SteppedEventsViewModel() }
    SteppedEventsContent(state = viewModel.state)
}
