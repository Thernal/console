package az.theternal.console.debugstepper.ui

import az.theternal.console.debugstepper.api.DebugStepper
import az.theternal.console.addon.api.autoinit.ConsoleAutoInitProvider

internal class DebugStepperAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        DebugStepper.install()
    }
}
