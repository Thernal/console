package az.theternal.console.details.compose

import az.theternal.console.runtime.ConsoleScope
import az.theternal.console.api.ConsoleAddon
import az.theternal.console.api.ConsoleNavGraph

object ConsoleDetailsAddon : ConsoleAddon {
    override fun onInstall(console: ConsoleScope) {}
    override fun navGraph(): ConsoleNavGraph {
        return DetailsNavGraph
    }
}
