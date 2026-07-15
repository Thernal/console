package io.thernal.console.stepper.ui.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.thernal.console.api.addon.ConsoleNavGraph
import io.thernal.console.stepper.ui.view.logdetail.SteppedLogDetailView

data class SteppedLogRoute(val logId: String) : NavKey

internal object StepperNavGraph : ConsoleNavGraph {
    override fun EntryProviderScope<NavKey>.routes() {
        entry<SteppedLogRoute> { route ->
            SteppedLogDetailView(logId = route.logId)
        }
    }
}
