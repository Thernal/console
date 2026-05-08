package az.theternal.console.debugstepper

import az.theternal.console.core.Console
import az.theternal.console.debugstepper.ui.DebugStepperNavGraph
import az.theternal.console.debugstepper.ui.overlay.DebugStepperOverlay
import az.theternal.console.ui.ConsoleNavigation
import az.theternal.console.ui.ConsoleOverlays
import az.theternal.console.ui.ConsolePlugin

object StepperPlugin : ConsolePlugin {
    override fun install() {
        Console.addObserver(DebugStepper)
        ConsoleNavigation.register(DebugStepperNavGraph)
        ConsoleOverlays.register { DebugStepperOverlay() }
    }
}
