package az.theternal.console.ui.observer

import az.theternal.console.addon.api.autoinit.ConsoleAutoInitProvider

internal class ConsoleUiAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        ConsoleLogObserver.register()
    }
}
