package az.theternal.console.stepper.compose.view.overlay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import az.theternal.console.compose.core.IntentHandler
import az.theternal.console.compose.core.StateHolder
import az.theternal.console.stepper.DebugStepper
import kotlinx.coroutines.launch

class DebugStepperOverlayViewModel : ViewModel(), StateHolder, IntentHandler<DebugStepperOverlayIntent> {
    override val state = DebugStepperOverlayState()

    init {
        viewModelScope.launch {
            DebugStepper.config.collect { config ->
                state.isEnabled.update { config.enabled }
                state.isPaused.update { config.paused }
                if (!config.enabled) state.isExpanded.update { false }
                syncDerived()
            }
        }
        viewModelScope.launch {
            DebugStepper.state.collect { syncDerived() }
        }
    }

    override val handler = Handler { intent ->
        when (intent) {
            DebugStepperOverlayIntent.ToggleEnabled -> DebugStepper.updateConfig { copy(enabled = !enabled) }

            DebugStepperOverlayIntent.TogglePaused -> DebugStepper.updateConfig { copy(paused = !paused) }

            DebugStepperOverlayIntent.StepNext -> DebugStepper.next()

            DebugStepperOverlayIntent.ToggleExpanded -> {
                if (DebugStepper.config.value.enabled) {
                    state.isExpanded.update { !this }
                }
            }
        }
    }

    private fun syncDerived() {
        val config = DebugStepper.config.value
        val stepperState = DebugStepper.state.value
        val canStep = stepperState.blockedLogId != null
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
