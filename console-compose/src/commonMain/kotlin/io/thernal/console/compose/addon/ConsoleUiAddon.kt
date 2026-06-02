package io.thernal.console.compose.addon

import io.thernal.console.api.addon.ConsoleAddon
import io.thernal.console.api.addon.ConsoleNavGraph
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.compose.ConsoleLogObserver
import io.thernal.console.compose.navigation.LogsNavGraph
import io.thernal.console.compose.navigation.LogsTab
import io.thernal.console.runtime.ConsoleScope

internal object ConsoleUiAddon : ConsoleAddon {
    override fun onInstall(console: ConsoleScope) {
        console.addObserver(ConsoleLogObserver)
    }

    override fun tab(): ConsoleTab = LogsTab
    override fun navGraph(): ConsoleNavGraph = LogsNavGraph
}
