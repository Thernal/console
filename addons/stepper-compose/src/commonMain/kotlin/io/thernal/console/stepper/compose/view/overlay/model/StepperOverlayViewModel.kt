package io.thernal.console.stepper.compose.view.overlay.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.compose.core.IntentHandler
import io.thernal.console.compose.core.StateHolder
import io.thernal.console.stepper.compose.Stepper
import io.thernal.console.stepper.compose.view.overlay.resolveCurrentStepDisplay
import io.thernal.console.stepper.compose.view.overlay.resolveDisplayTag
import io.thernal.console.stepper.compose.view.overlay.resolveStatusText
import io.thernal.console.stepper.compose.view.overlay.resolveStatusTone
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class StepperOverlayViewModel : ViewModel(), StateHolder, IntentHandler<StepperOverlayIntent> {
    val state = StepperOverlayState()

    init {
        syncDerived()
        viewModelScope.launch {
            combine(Stepper.config, Stepper.state) { _, _ -> Unit }.collect { syncDerived() }
        }
    }

    override val handler = onIntentUpdate { intent ->
        when (intent) {
            StepperOverlayIntent.ToggleEnabled -> Stepper.updateConfig { copy(enabled = !enabled) }

            StepperOverlayIntent.TogglePaused -> Stepper.updateConfig { copy(paused = !paused) }

            StepperOverlayIntent.StepNext -> Stepper.next()

            StepperOverlayIntent.ToggleExpanded -> {
                if (state.isEnabled.value) {
                    state.isExpanded.update { !this }
                }
            }
        }
    }

    private fun syncDerived() {
        val config = Stepper.config.value
        val stepperState = Stepper.state.value
        val canStep = stepperState.blockedLogId != null

        snapshot {
            if (!config.enabled) state.isExpanded.set(false)
            state.isEnabled.set(config.enabled)
            state.isPaused.set(config.paused)
            state.currentLog.set(stepperState.steppedEvents.lastOrNull())
            state.displayTag.set(resolveDisplayTag(stepperState, config))
            state.canStep.set(canStep)
            state.caughtCount.set(stepperState.steppedEvents.size)
            state.statusText.set(resolveStatusText(stepperState, canStep, config.enabled, config.paused))
            state.statusTone.set(resolveStatusTone(canStep, config.enabled, config.paused))
            state.currentStepDisplay.set(resolveCurrentStepDisplay(stepperState))
        }
    }
}
