package io.thernal.console.settings.ui

import io.thernal.console.api.autoinit.ConsoleAutoInitProvider
import io.thernal.console.settings.ui.addon.SettingsAddon

internal class SettingsAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        SettingsAddon.install()
    }
}
