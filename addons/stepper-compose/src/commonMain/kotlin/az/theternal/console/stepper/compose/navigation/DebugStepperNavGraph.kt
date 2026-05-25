package az.theternal.console.stepper.compose.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import az.theternal.console.api.addon.ConsoleNavGraph
import az.theternal.console.stepper.compose.view.events.SteppedEventsView

data object SteppedEventsRoute : NavKey

internal object DebugStepperNavGraph : ConsoleNavGraph {
    override fun EntryProviderScope<NavKey>.routes() {
        entry<SteppedEventsRoute> {
            SteppedEventsView()
        }
    }
}
