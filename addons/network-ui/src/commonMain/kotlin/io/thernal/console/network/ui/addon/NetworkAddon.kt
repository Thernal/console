package io.thernal.console.network.ui.addon

import io.thernal.console.api.addon.ConsoleAddon
import io.thernal.console.api.ui.LogRendererRegistry
import io.thernal.console.network.NetworkLog
import io.thernal.console.network.ui.addon.NetworkLogRenderer
import io.thernal.console.runtime.console.ConsoleScope

object NetworkAddon : ConsoleAddon {
    override fun onInstall(console: ConsoleScope) {
        LogRendererRegistry.register<NetworkLog.Request>(NetworkLogRenderer)
        LogRendererRegistry.register<NetworkLog.Response>(NetworkLogRenderer)
    }
}
