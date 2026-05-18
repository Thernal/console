package az.theternal.console.compose

import az.theternal.console.api.autoinit.ConsoleAutoInitProvider
import az.theternal.console.compose.navigation.ConsoleLogObserver

internal class ConsoleUiAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        ConsoleLogObserver.register()
    }
}
