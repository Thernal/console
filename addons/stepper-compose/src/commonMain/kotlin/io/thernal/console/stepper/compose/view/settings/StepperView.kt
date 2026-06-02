package io.thernal.console.stepper.compose.view.settings

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.stepper.compose.view.settings.model.StepperViewModel

@Composable
internal fun StepperView() {
    val viewModel = viewModel { StepperViewModel() }
    StepperContent(
        state = viewModel.state,
        dispatch = viewModel::dispatch,
    )
}
