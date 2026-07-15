package io.thernal.console.stepper.ui.view.caught.model

import androidx.compose.runtime.Stable
import io.thernal.console.core.log.Log
import io.thernal.console.ui.core.ViewState

@Stable
class StepperCaughtState : ViewState() {
    val steppedEvents = field(emptyList<Log>())
}
