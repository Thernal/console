package az.theternal.console.stepper.compose

import az.theternal.console.api.autoinit.ConsoleAutoInitProvider

internal class DebugStepperAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        DebugStepperAddon.install()
    }
}
