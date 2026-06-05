package io.thernal.console.stepper.compose.view.events.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.compose.core.StateHolder
import io.thernal.console.stepper.compose.stepper.Stepper
import kotlinx.coroutines.launch

class SteppedEventsViewModel : ViewModel(), StateHolder {
    val state = SteppedEventsState()

    init {
        viewModelScope.launch {
            Stepper.state.collect { state.steppedEvents.set(it.steppedEvents) }
        }
    }
}
