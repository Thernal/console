package io.thernal.console.agent.ui

import io.thernal.console.agent.ui.addon.AgentAddon
import io.thernal.console.api.autoinit.ConsoleInitializer

internal class AgentAutoInit : ConsoleInitializer {
    override fun init() {
        AgentAddon.install()
    }
}
