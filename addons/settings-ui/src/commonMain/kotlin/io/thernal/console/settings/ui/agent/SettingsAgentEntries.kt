package io.thernal.console.settings.ui.agent

import io.thernal.console.agent.AgentEntries
import io.thernal.console.agent.agentEntries
import io.thernal.console.settings.SettingsRegistry

/**
 * What the settings addon shares with an AI agent: every addon's settings in one place, so no addon needs config
 * tools of its own. Changes go through the same path as the Settings tab; the agent's own section is read-only.
 */
internal fun settingsAgentEntries(): AgentEntries {
    return agentEntries(
        id = "settings",
        title = "Settings",
        about = "Every addon's settings: read them, and change them while actions are on",
    ) {
        tool(name = "settings", description = "Sections and their entries: value, kind, range or options, enabled") {
            val section = optionalText(key = "section", description = "Only this section id")
            run { call -> settingsText(SettingsRegistry.sections.value, call[section]) }
        }
        action(
            name = "settings_set",
            description = "Changes one entry like the Settings tab does: at once, and kept across relaunches",
        ) {
            val section = text(key = "section", description = "Section id, from settings")
            val key = text(key = "key", description = "Entry key, from settings")
            val value = text(
                key = "value",
                description = "true or false, a number, an option name (or none), or tags separated by commas",
            )
            run { call -> setEntry(SettingsRegistry.sections.value, call[section], call[key], call[value]) }
        }
        action(
            name = "settings_reset",
            description = "Resets one section, or every section but the agent's, to the code defaults",
        ) {
            val section = optionalText(key = "section", description = "Section id; every section when left out")
            run { call -> resetSettings(SettingsRegistry.sections.value, call[section]) }
        }
    }
}
