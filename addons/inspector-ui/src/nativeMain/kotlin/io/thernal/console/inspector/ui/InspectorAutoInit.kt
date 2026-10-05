package io.thernal.console.inspector.ui

import io.thernal.console.api.autoinit.consoleAddonInit
import io.thernal.console.inspector.ui.addon.InspectorAddon
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = consoleAddonInit { InspectorAddon.install() }
