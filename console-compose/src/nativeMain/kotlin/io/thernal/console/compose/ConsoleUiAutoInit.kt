package io.thernal.console.compose

import io.thernal.console.compose.addon.ConsoleUiAddon
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = object {
    init {
        ConsoleUiAddon.install()
    }
}
