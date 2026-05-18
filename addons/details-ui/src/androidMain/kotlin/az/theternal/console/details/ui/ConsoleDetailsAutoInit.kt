package az.theternal.console.details.ui

import az.theternal.console.addon.api.autoinit.ConsoleAutoInitProvider

internal class ConsoleDetailsAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        ConsoleDetailsAddon.install()
    }
}
