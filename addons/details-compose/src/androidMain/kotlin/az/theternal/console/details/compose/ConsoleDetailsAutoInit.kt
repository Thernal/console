package az.theternal.console.details.compose

import az.theternal.console.api.autoinit.ConsoleAutoInitProvider

internal class ConsoleDetailsAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        ConsoleDetailsAddon.install()
    }
}
