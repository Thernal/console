package io.thernal.console.details.ui

import io.thernal.console.api.autoinit.consoleAddonInit
import io.thernal.console.details.ui.addon.ConsoleDetailsAddon
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = consoleAddonInit { ConsoleDetailsAddon.install() }
