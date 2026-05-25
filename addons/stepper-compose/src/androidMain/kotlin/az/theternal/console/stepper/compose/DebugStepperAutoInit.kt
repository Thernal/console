package az.theternal.console.stepper.compose

import az.theternal.console.api.autoinit.ConsoleAutoInitProvider
import az.theternal.console.stepper.compose.addon.DebugStepperAddon

internal class DebugStepperAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        DebugStepperAddon.install()
    }
}
