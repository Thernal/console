package io.thernal.console.network.ui

import io.thernal.console.api.autoinit.ConsoleInitializer
import io.thernal.console.network.ui.addon.NetworkAddon

internal class ConsoleNetworkAutoInit : ConsoleInitializer {
    override fun init() {
        NetworkAddon.install()
    }
}
