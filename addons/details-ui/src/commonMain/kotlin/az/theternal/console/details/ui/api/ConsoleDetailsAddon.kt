package az.theternal.console.details.ui.api

import az.theternal.console.runtime.api.ConsoleScope
import az.theternal.console.ui.ConsoleAddon
import az.theternal.console.ui.nav.ConsoleNavGraph
import az.theternal.console.details.ui.DetailsNavGraph

object ConsoleDetailsAddon : ConsoleAddon {
    override fun onInstall(console: ConsoleScope) {}
    override fun navGraph(): ConsoleNavGraph {
        return DetailsNavGraph
    }
}
