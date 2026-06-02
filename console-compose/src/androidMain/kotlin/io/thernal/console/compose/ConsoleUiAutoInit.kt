package io.thernal.console.compose

import io.thernal.console.api.autoinit.ConsoleAutoInitProvider
import io.thernal.console.compose.addon.ConsoleUiAddon

internal class ConsoleUiAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        ConsoleUiAddon.install()
    }
}
