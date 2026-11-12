package io.thernal.console.agent.ui

import io.thernal.console.agent.ui.addon.AgentAddon
import io.thernal.console.api.autoinit.consoleAddonInit
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = consoleAddonInit { AgentAddon.install() }
