package az.theternal.console.stepper.compose

import az.theternal.console.api.autoinit.ConsoleAutoInitProvider
import az.theternal.console.stepper.compose.addon.StepperAddon

internal class StepperAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        StepperAddon.install()
    }
}
