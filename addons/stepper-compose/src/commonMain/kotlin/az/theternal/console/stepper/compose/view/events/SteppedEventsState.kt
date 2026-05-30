package az.theternal.console.stepper.compose.view.events

import az.theternal.console.compose.core.ViewState
import az.theternal.console.runtime.Log

class SteppedEventsState : ViewState() {
    val events = field(emptyList<Log>())
    val count = field(0)
}
