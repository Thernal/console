package az.theternal.console.details.ui

import az.theternal.console.runtime.api.ConsoleScope
import az.theternal.console.addon.api.ConsoleAddon
import az.theternal.console.addon.api.nav.ConsoleNavGraph
import az.theternal.console.details.ui.DetailsNavGraph

object ConsoleDetailsAddon : ConsoleAddon {
    override fun onInstall(console: ConsoleScope) {}
    override fun navGraph(): ConsoleNavGraph {
        return DetailsNavGraph
    }
}
