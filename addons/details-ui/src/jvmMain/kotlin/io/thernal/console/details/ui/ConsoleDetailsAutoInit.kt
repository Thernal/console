package io.thernal.console.details.ui

import io.thernal.console.api.autoinit.ConsoleInitializer
import io.thernal.console.details.ui.addon.ConsoleDetailsAddon

internal class ConsoleDetailsAutoInit : ConsoleInitializer {
    override fun init() {
        ConsoleDetailsAddon.install()
    }
}
