package az.theternal.console.stepper.compose.view.overlay.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import az.theternal.console.compose.core.IntentHandler
import az.theternal.console.compose.core.StateHolder
import az.theternal.console.stepper.Stepper
import az.theternal.console.stepper.compose.view.overlay.resolveCurrentStepDisplay
import az.theternal.console.stepper.compose.view.overlay.resolveDisplayTag
import az.theternal.console.stepper.compose.view.overlay.resolveStatusText
import az.theternal.console.stepper.compose.view.overlay.resolveStatusTone
import kotlinx.coroutines.launch

class StepperOverlayViewModel : ViewModel(), StateHolder, IntentHandler<StepperOverlayIntent> {
    val state = StepperOverlayState()

    init {
        viewModelScope.launch { Stepper.config.collect { syncDerived() } }
        viewModelScope.launch { Stepper.state.collect { syncDerived() } }
    }

    override val handler = Handler { intent ->
        when (intent) {
            StepperOverlayIntent.ToggleEnabled -> Stepper.updateConfig { copy(enabled = !enabled) }

            StepperOverlayIntent.TogglePaused -> Stepper.updateConfig { copy(paused = !paused) }

            StepperOverlayIntent.StepNext -> Stepper.next()

            StepperOverlayIntent.ToggleExpanded -> {
                if (Stepper.config.value.enabled) {
                    state.isExpanded.update { !this }
                }
            }
        }
    }

    private fun syncDerived() {
        val config = Stepper.config.value
        val stepperState = Stepper.state.value
        val canStep = stepperState.blockedLogId != null
        if (!config.enabled) state.isExpanded.update { false }
        state.isEnabled.update { config.enabled }
        state.isPaused.update { config.paused }
        state.currentLog.update { stepperState.steppedEvents.lastOrNull() }
        state.displayTag.update { resolveDisplayTag(stepperState, config) }
        state.canStep.update { canStep }
        state.caughtCount.update { stepperState.steppedEvents.size }
        state.statusText.update { resolveStatusText(stepperState, canStep, config.enabled, config.paused) }
        state.statusTone.update { resolveStatusTone(canStep, config.enabled, config.paused) }
        state.currentStepDisplay.update { resolveCurrentStepDisplay(stepperState) }
    }
}
