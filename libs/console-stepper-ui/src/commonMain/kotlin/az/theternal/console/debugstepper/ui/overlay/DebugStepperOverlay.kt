package az.theternal.console.debugstepper.ui.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import az.theternal.console.debugstepper.DebugStepper

@Composable
internal fun DebugStepperOverlay() {
    val stepperState by DebugStepper.state.collectAsState()
    val uiState = buildDebugStepperOverlayUiState(stepperState)
    var isFullscreen by rememberSaveable { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        DebugStepperFloatingCard(
            uiState = uiState,
            stepperState = stepperState,
            onOpenFullscreen = { isFullscreen = true },
        )
        DebugStepperFullscreenPanel(
            visible = isFullscreen,
            uiState = uiState,
            stepperState = stepperState,
            onDismiss = { isFullscreen = false },
        )
    }
}
