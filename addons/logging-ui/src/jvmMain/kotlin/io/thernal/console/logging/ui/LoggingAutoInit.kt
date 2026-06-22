package io.thernal.console.logging.ui

import io.thernal.console.api.autoinit.ConsoleInitializer
import io.thernal.console.logging.ui.addon.LoggingAddon

internal class LoggingAutoInit : ConsoleInitializer {
    override fun init() {
        LoggingAddon.install()
    }
}
