package io.thernal.console.stepper.ui.view.caught

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.stepper.ui.view.caught.model.StepperCaughtViewModel

@Composable
internal fun StepperCaughtView() {
    val viewModel = viewModel { StepperCaughtViewModel() }
    StepperCaughtContent(steppedEvents = viewModel.state.steppedEvents)
}
