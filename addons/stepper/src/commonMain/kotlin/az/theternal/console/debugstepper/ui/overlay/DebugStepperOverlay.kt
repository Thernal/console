package az.theternal.console.debugstepper.ui.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import az.theternal.console.debugstepper.DebugStepper

@Composable
internal fun DebugStepperOverlay() {
    val config by DebugStepper.config.collectAsState()
    val state by DebugStepper.state.collectAsState()
    val uiState = buildDebugStepperOverlayUiState(state, config)

    Box(Modifier.fillMaxSize()) {
        DebugStepperFloatingCard(uiState = uiState)
    }
}
