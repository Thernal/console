package az.theternal.console.compose

import az.theternal.console.api.autoinit.ConsoleAutoInitProvider
import az.theternal.console.compose.addon.ConsoleUiAddon

internal class ConsoleUiAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        ConsoleUiAddon.install()
    }
}
