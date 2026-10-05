package io.thernal.console.stepper.ui.view.overlay.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.ui.core.StateHolder
import io.thernal.console.stepper.ui.stepper.Stepper
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class StepperOverlayViewModel : ViewModel(), StateHolder {
    val state = StepperOverlayState()

    init {
        syncDerived()
        viewModelScope.launch {
            combine(Stepper.config, Stepper.state) { _, _ -> Unit }.collect { syncDerived() }
        }
    }

    private fun syncDerived() {
        val config = Stepper.config.value
        val stepperState = Stepper.state.value

        snapshot {
            state.isEnabled.set(config.enabled)
            state.isPaused.set(config.paused)
            state.pendingLogs.set(stepperState.pendingLogs)
            state.blockedLogId.set(stepperState.blockedLogId)
            state.blockedTag.set(stepperState.blockedTag)
            state.steppedEvents.set(stepperState.steppedEvents)
        }
    }
}
