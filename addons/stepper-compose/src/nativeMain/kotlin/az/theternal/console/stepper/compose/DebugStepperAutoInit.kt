package az.theternal.console.stepper.compose

import az.theternal.console.api.autoinit.consoleAddonInit
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = consoleAddonInit { DebugStepperAddon.install() }
