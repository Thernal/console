package az.theternal.console.stepper.compose

import az.theternal.console.api.autoinit.consoleAddonInit
import az.theternal.console.stepper.compose.addon.StepperAddon
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = consoleAddonInit { StepperAddon.install() }
