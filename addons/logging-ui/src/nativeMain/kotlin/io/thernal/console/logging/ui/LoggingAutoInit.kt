package io.thernal.console.logging.ui

import io.thernal.console.logging.ui.addon.LoggingAddon
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = object {
    init {
        LoggingAddon.install()
    }
}
