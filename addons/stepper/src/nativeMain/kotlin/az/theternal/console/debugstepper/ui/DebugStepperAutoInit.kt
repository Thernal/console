package az.theternal.console.debugstepper.ui

import az.theternal.console.debugstepper.api.DebugStepper
import az.theternal.console.addon.api.autoinit.consoleAddonInit
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class)
@Suppress("unused")
private val init = consoleAddonInit { DebugStepper.install() }
