package io.thernal.console.stepper.ui.view.events.model

import androidx.compose.runtime.Stable
import io.thernal.console.ui.core.ViewState
import io.thernal.console.ui.core.derive
import io.thernal.console.runtime.log.Log

@Stable
class SteppedEventsState : ViewState() {
    val steppedEvents = field(emptyList<Log>())

    val events = steppedEvents.derive { currentEvents ->
        currentEvents.asReversed()
    }
}
