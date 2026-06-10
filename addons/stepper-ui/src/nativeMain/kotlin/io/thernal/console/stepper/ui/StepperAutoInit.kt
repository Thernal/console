package io.thernal.console.stepper.ui

import io.thernal.console.api.autoinit.consoleAddonInit
import io.thernal.console.stepper.ui.addon.StepperAddon
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = consoleAddonInit { StepperAddon.install() }
