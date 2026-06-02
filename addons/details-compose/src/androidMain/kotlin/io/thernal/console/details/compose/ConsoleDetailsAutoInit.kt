package io.thernal.console.details.compose

import io.thernal.console.api.autoinit.ConsoleAutoInitProvider
import io.thernal.console.details.compose.addon.ConsoleDetailsAddon

internal class ConsoleDetailsAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        ConsoleDetailsAddon.install()
    }
}
