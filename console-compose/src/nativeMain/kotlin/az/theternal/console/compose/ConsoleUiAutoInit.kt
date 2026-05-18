package az.theternal.console.compose

import az.theternal.console.compose.navigation.ConsoleLogObserver
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = object {
    init {
        ConsoleLogObserver.register()
    }
}
