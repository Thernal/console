package az.theternal.console.details.ui

import az.theternal.console.details.ui.api.ConsoleDetailsAddon
import az.theternal.console.ui.autoinit.consoleAddonInit
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = consoleAddonInit { ConsoleDetailsAddon.install() }
