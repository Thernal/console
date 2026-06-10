package io.thernal.console.logging.ui.addon

import io.thernal.console.api.addon.ConsoleAddon
import io.thernal.console.api.addon.ConsoleNavGraph
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.api.ui.LogRendererRegistry
import io.thernal.console.logging.ui.ConsoleLogObserver
import io.thernal.console.logging.ui.navigation.LogsNavGraph
import io.thernal.console.logging.ui.navigation.LogsTab
import io.thernal.console.runtime.console.ConsoleScope
import io.thernal.console.runtime.log.BasicLog

internal object LoggingAddon : ConsoleAddon {
    override fun onInstall(console: ConsoleScope) {
        console.addObserver(ConsoleLogObserver)
        LogRendererRegistry.register<BasicLog>(BasicLogRenderer())
    }

    override fun tab(): ConsoleTab = LogsTab
    override fun navGraph(): ConsoleNavGraph = LogsNavGraph
}
