package io.thernal.console.network.ui

import io.thernal.console.api.autoinit.ConsoleAutoInitProvider
import io.thernal.console.network.ui.addon.NetworkAddon

internal class ConsoleNetworkAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        NetworkAddon.install()
    }
}
