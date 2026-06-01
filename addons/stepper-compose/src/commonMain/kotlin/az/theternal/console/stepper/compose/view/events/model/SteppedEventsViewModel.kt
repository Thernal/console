package az.theternal.console.stepper.compose.view.events.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import az.theternal.console.compose.core.StateHolder
import az.theternal.console.stepper.Stepper
import kotlinx.coroutines.launch

class SteppedEventsViewModel : ViewModel(), StateHolder {
    val state = SteppedEventsState()

    init {
        viewModelScope.launch {
            Stepper.state.collect { state.events.set(it.steppedEvents.asReversed()) }
        }
    }
}
