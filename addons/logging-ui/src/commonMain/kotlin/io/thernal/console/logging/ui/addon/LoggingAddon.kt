@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.logging.ui.addon

import io.thernal.console.api.addon.ConsoleAddon
import io.thernal.console.api.addon.ConsoleNavGraph
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.api.ui.LogRendererRegistry
import io.thernal.console.logging.ui.ConsoleLogObserver
import io.thernal.console.logging.ui.navigation.LogsNavGraph
import io.thernal.console.logging.ui.navigation.LogsTab
import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.runtime.console.Console
import io.thernal.console.core.log.BasicLog
import io.thernal.console.settings.SettingsRegistry

internal object LoggingAddon : ConsoleAddon {
    override fun onInstall() {
        Console.addObserver(ConsoleLogObserver)
        LogRendererRegistry.register<BasicLog>(BasicLogRenderer())
        SettingsRegistry.register(loggingSettingsSection())
    }

    override fun tab(): ConsoleTab = LogsTab
    override fun navGraph(): ConsoleNavGraph = LogsNavGraph
}
