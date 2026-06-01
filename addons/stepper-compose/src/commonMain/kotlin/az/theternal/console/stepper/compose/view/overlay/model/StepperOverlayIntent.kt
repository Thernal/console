package az.theternal.console.stepper.compose.view.overlay.model

import az.theternal.console.compose.core.Intent

sealed interface StepperOverlayIntent : Intent {
    data object ToggleEnabled : StepperOverlayIntent
    data object TogglePaused : StepperOverlayIntent
    data object StepNext : StepperOverlayIntent
    data object ToggleExpanded : StepperOverlayIntent
}
