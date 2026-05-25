package az.theternal.console.details.compose

import az.theternal.console.api.autoinit.ConsoleAutoInitProvider
import az.theternal.console.details.compose.addon.ConsoleDetailsAddon

internal class ConsoleDetailsAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        ConsoleDetailsAddon.install()
    }
}
