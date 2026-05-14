package az.theternal.console.ui.observer

import az.theternal.console.ui.autoinit.ConsoleAutoInitProvider

internal class ConsoleUiAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        ConsoleLogObserver.register()
    }
}
