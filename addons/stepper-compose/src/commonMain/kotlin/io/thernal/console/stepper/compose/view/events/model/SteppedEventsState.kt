package io.thernal.console.stepper.compose.view.events.model

import androidx.compose.runtime.Stable
import io.thernal.console.compose.core.ViewState
import io.thernal.console.runtime.log.Log

@Stable
class SteppedEventsState : ViewState() {
    val events = field(emptyList<Log>())
}
