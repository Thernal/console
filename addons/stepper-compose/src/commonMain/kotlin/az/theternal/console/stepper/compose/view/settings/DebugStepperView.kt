package az.theternal.console.stepper.compose.view.settings

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
internal fun DebugStepperView() {
    val viewModel = viewModel { DebugStepperViewModel() }
    DebugStepperContent(
        state = viewModel.state,
        dispatch = viewModel::dispatch,
    )
}
