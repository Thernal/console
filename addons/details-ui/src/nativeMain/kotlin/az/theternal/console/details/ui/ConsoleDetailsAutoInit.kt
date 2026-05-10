package az.theternal.console.details.ui

import az.theternal.console.ui.ConsoleNavigation
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = object {
    init {
        ConsoleNavigation.register(DetailsNavGraph)
    }
}
