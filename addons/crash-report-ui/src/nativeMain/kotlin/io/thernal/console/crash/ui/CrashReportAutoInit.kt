package io.thernal.console.crash.ui

import io.thernal.console.api.autoinit.consoleAddonInit
import io.thernal.console.crash.ui.addon.CrashReportAddon
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = consoleAddonInit { CrashReportAddon.install() }
