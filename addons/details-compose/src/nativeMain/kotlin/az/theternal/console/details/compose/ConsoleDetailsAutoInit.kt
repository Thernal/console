package az.theternal.console.details.compose

import az.theternal.console.api.autoinit.consoleAddonInit
import az.theternal.console.details.compose.addon.ConsoleDetailsAddon
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = consoleAddonInit { ConsoleDetailsAddon.install() }
