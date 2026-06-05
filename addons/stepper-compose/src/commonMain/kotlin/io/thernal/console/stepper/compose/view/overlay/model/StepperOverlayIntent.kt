package io.thernal.console.stepper.compose.view.overlay.model

import io.thernal.console.compose.core.ViewIntent

sealed interface StepperOverlayIntent : ViewIntent {
    data object ToggleEnabled : StepperOverlayIntent
    data object TogglePaused : StepperOverlayIntent
    data object StepNext : StepperOverlayIntent
    data object ToggleExpanded : StepperOverlayIntent
}
