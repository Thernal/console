@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.network.ui.addon

import io.thernal.console.agent.AgentRegistry
import io.thernal.console.network.ui.agent.NetworkCallObserver
import io.thernal.console.network.ui.agent.networkAgentEntries
import io.thernal.console.runtime.console.Console
import io.thernal.console.api.addon.ConsoleAddon
import io.thernal.console.api.ui.LogRendererRegistry
import io.thernal.console.network.NetworkLog
import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.settings.SettingsRegistry

object NetworkAddon : ConsoleAddon {
    private val callObserver = NetworkCallObserver()

    override fun onInstall() {
        LogRendererRegistry.register<NetworkLog.Request>(NetworkLogRenderer)
        LogRendererRegistry.register<NetworkLog.Response>(NetworkLogRenderer)
        SettingsRegistry.register(networkSettingsSection())
        Console.addObserver(callObserver)
        AgentRegistry.register(networkAgentEntries(callObserver))
    }
}
