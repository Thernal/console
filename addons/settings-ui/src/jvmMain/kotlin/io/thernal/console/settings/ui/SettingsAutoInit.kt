package io.thernal.console.settings.ui

import io.thernal.console.api.autoinit.ConsoleInitializer
import io.thernal.console.settings.ui.addon.SettingsAddon

internal class SettingsAutoInit : ConsoleInitializer {
    override fun init() {
        SettingsAddon.install()
    }
}
