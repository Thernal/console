package io.thernal.console.network.ui

import io.thernal.console.api.autoinit.consoleAddonInit
import io.thernal.console.network.ui.addon.NetworkAddon
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = consoleAddonInit { NetworkAddon.install() }
