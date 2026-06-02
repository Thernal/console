package io.thernal.console.details.compose

import io.thernal.console.api.autoinit.consoleAddonInit
import io.thernal.console.details.compose.addon.ConsoleDetailsAddon
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = consoleAddonInit { ConsoleDetailsAddon.install() }
