package io.thernal.console.agent.ui

import io.thernal.console.agent.ui.addon.AgentAddon
import io.thernal.console.api.autoinit.ConsoleAutoInitProvider

internal class AgentAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        AgentAddon.install()
    }
}
