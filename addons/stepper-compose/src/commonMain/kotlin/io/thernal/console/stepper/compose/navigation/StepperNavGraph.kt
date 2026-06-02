package io.thernal.console.stepper.compose.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.thernal.console.api.addon.ConsoleNavGraph
import io.thernal.console.stepper.compose.view.events.SteppedEventsView

data object SteppedEventsRoute : NavKey

internal object StepperNavGraph : ConsoleNavGraph {
    override fun EntryProviderScope<NavKey>.routes() {
        entry<SteppedEventsRoute> {
            SteppedEventsView()
        }
    }
}
