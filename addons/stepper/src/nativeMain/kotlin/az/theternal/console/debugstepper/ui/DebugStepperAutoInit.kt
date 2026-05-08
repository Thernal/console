package az.theternal.console.debugstepper.ui

import az.theternal.console.core.Console
import az.theternal.console.debugstepper.DebugStepper
import az.theternal.console.debugstepper.ui.overlay.DebugStepperOverlay
import az.theternal.console.ui.ConsoleNavigation
import az.theternal.console.ui.ConsoleOverlays
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.EagerInitialization

@EagerInitialization
@OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
@Suppress("unused")
private val init = object {
    init {
        Console.addObserver(DebugStepper)
        ConsoleNavigation.register(DebugStepperNavGraph)
        ConsoleOverlays.register { DebugStepperOverlay() }
    }
}
