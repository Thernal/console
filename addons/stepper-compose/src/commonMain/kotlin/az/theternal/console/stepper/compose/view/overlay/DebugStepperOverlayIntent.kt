package az.theternal.console.stepper.compose.view.overlay

import az.theternal.console.compose.core.Intent

sealed interface DebugStepperOverlayIntent : Intent {
    data object ToggleEnabled : DebugStepperOverlayIntent
    data object TogglePaused : DebugStepperOverlayIntent
    data object StepNext : DebugStepperOverlayIntent
    data object ToggleExpanded : DebugStepperOverlayIntent
}
