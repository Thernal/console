@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.agent.ui.addon

import io.thernal.console.agent.ui.bridge.AgentBridge
import io.thernal.console.api.addon.ConsoleAddon
import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.settings.SettingsRegistry

/**
 * Agent bridge: shares what the addons registered in `AgentRegistry` with the `console-mcp` hub, so an AI agent can
 * read it (and, when allowed, act on the app) over MCP.
 */
object AgentAddon : ConsoleAddon {
    private val bridge = AgentBridge()

    override fun onInstall() {
        SettingsRegistry.register(agentSettingsSection())
        bridge.start()
    }
}
