package io.thernal.console.stepper.ui.view.settings

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.stepper.ui.stepper.Stepper
import io.thernal.console.stepper.ui.view.settings.model.StepperSettingsViewModel

@Composable
internal fun StepperSettingsView() {
    val viewModel = viewModel { StepperSettingsViewModel() }
    StepperSettingsContent(
        state = viewModel.state,
        dispatch = viewModel::dispatch,
        onStepperDispatch = Stepper::dispatch,
    )
}
