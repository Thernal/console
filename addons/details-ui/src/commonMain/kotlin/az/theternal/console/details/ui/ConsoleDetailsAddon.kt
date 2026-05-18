package az.theternal.console.details.ui

import az.theternal.console.ConsoleScope
import az.theternal.console.addon.api.ConsoleAddon
import az.theternal.console.addon.api.nav.ConsoleNavGraph

object ConsoleDetailsAddon : ConsoleAddon {
    override fun onInstall(console: ConsoleScope) {}
    override fun navGraph(): ConsoleNavGraph {
        return DetailsNavGraph
    }
}
