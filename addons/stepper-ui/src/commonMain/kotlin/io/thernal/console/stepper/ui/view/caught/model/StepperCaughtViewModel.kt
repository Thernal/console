package io.thernal.console.stepper.ui.view.caught.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.stepper.ui.stepper.Stepper
import io.thernal.console.ui.core.StateHolder
import kotlinx.coroutines.launch

class StepperCaughtViewModel : ViewModel(), StateHolder {
    val state = StepperCaughtState()

    init {
        viewModelScope.launch {
            Stepper.state.collect { state.steppedEvents.set(it.steppedEvents) }
        }
    }
}
