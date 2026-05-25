package az.theternal.console.compose.addon

import az.theternal.console.api.addon.ConsoleAddon
import az.theternal.console.api.addon.ConsoleNavGraph
import az.theternal.console.api.addon.ConsoleTab
import az.theternal.console.compose.ConsoleLogObserver
import az.theternal.console.compose.navigation.LogsNavGraph
import az.theternal.console.compose.navigation.LogsTab
import az.theternal.console.runtime.ConsoleScope

internal object ConsoleUiAddon : ConsoleAddon {
    override fun onInstall(console: ConsoleScope) {
        console.addObserver(ConsoleLogObserver)
    }

    override fun tab(): ConsoleTab = LogsTab
    override fun navGraph(): ConsoleNavGraph = LogsNavGraph
}
