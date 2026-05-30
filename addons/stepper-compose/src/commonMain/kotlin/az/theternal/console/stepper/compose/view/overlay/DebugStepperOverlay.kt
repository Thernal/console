package az.theternal.console.stepper.compose.view.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import az.theternal.console.stepper.compose.view.overlay.model.DebugStepperOverlayViewModel

@Composable
internal fun DebugStepperOverlay() {
    val viewModel = viewModel { DebugStepperOverlayViewModel() }

    Box(Modifier.fillMaxSize()) {
        DebugStepperOverlayContent(
            state = viewModel.state,
            dispatch = viewModel::dispatch,
        )
    }
}
