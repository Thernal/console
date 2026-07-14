package io.thernal.console.settings.ui.addon

import io.thernal.console.api.addon.ConsoleAddon
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.settings.SettingsRegistry
import io.thernal.console.settings.SettingsSection
import io.thernal.console.settings.settingsCustom
import io.thernal.console.settings.ui.navigation.SettingsTab
import io.thernal.console.settings.ui.store.SettingsStore

@OptIn(ConsoleInternalApi::class)
object SettingsAddon : ConsoleAddon {

    override fun onInstall() {
        SettingsStore.install()
        SettingsRegistry.register(generalSection())
    }

    override fun tab(): ConsoleTab = SettingsTab

    private fun generalSection(): SettingsSection {
        return settingsCustom(id = "general", title = "General", order = Int.MAX_VALUE) {
            item { ResetAllSettingsButton(onClick = SettingsStore::clearAll) }
        }
    }
}
