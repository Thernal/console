package az.theternal.console.stepper.compose.view.events.model

import androidx.compose.runtime.Stable
import az.theternal.console.compose.core.ViewState
import az.theternal.console.runtime.Log

@Stable
class SteppedEventsState : ViewState() {
    val events = field(emptyList<Log>())
    val count = field(0)
}
