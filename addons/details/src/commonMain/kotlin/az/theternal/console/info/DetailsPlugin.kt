package az.theternal.console.info

import az.theternal.console.info.ui.InfoNavGraph
import az.theternal.console.ui.ConsoleNavigation
import az.theternal.console.ui.ConsolePlugin

object DetailsPlugin : ConsolePlugin {
    override fun install() {
        ConsoleNavigation.register(InfoNavGraph)
    }
}
