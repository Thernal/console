package az.theternal.console.stepper.compose.view.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import az.theternal.console.stepper.compose.view.overlay.model.StepperOverlayViewModel

@Composable
internal fun StepperOverlay() {
    val viewModel = viewModel { StepperOverlayViewModel() }

    Box(Modifier.fillMaxSize()) {
        StepperOverlayContent(
            state = viewModel.state,
            dispatch = viewModel::dispatch,
        )
    }
}
