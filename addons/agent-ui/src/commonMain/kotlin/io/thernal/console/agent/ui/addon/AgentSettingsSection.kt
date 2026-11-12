package io.thernal.console.agent.ui.addon

import io.thernal.console.agent.ConsoleAgent
import io.thernal.console.settings.SettingsSection
import io.thernal.console.settings.settingsEntries

private const val ORDER = 60

internal fun agentSettingsSection(): SettingsSection {
    return settingsEntries(
        id = "agent",
        title = "Agent",
        order = ORDER,
        config = ConsoleAgent.config,
        update = ConsoleAgent::updateConfig,
    ) {
        toggle(
            key = "enabled",
            title = "Share with AI agents",
            description = "Connects to the console-mcp hub on this computer and shares what the addons offer",
            read = { it.enabled },
            write = { copy(enabled = it) },
        )
        toggle(
            key = "enableActions",
            title = "Allow actions",
            description = "Lets an agent change the app, for example step the stepper or change settings",
            read = { it.enableActions },
            write = { copy(enableActions = it) },
        )
    }
}
