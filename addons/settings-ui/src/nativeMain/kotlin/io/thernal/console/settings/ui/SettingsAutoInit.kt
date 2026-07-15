package io.thernal.console.settings.ui

import io.thernal.console.api.autoinit.consoleAddonInit
import io.thernal.console.settings.ui.addon.SettingsAddon
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = consoleAddonInit { SettingsAddon.install() }
