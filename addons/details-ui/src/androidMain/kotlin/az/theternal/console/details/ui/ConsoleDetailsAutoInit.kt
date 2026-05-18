package az.theternal.console.details.ui

import az.theternal.console.details.ui.api.ConsoleDetailsAddon
import az.theternal.console.ui.autoinit.ConsoleAutoInitProvider

internal class ConsoleDetailsAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        ConsoleDetailsAddon.install()
    }
}
