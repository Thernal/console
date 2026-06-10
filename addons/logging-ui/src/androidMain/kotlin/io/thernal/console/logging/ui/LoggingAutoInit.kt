package io.thernal.console.logging.ui

import io.thernal.console.api.autoinit.ConsoleAutoInitProvider
import io.thernal.console.logging.ui.addon.LoggingAddon

internal class LoggingAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        LoggingAddon.install()
    }
}
