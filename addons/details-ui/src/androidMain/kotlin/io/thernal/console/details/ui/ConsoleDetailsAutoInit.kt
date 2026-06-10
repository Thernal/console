package io.thernal.console.details.ui

import io.thernal.console.api.autoinit.ConsoleAutoInitProvider
import io.thernal.console.details.ui.addon.ConsoleDetailsAddon

internal class ConsoleDetailsAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        ConsoleDetailsAddon.install()
    }
}
