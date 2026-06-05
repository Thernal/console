package io.thernal.console.network.compose

import io.thernal.console.api.autoinit.ConsoleAutoInitProvider
import io.thernal.console.network.compose.addon.NetworkAddon

internal class ConsoleNetworkAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        NetworkAddon.install()
    }
}
