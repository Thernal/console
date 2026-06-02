package io.thernal.console.stepper.compose

import io.thernal.console.api.autoinit.ConsoleAutoInitProvider
import io.thernal.console.stepper.compose.addon.StepperAddon

internal class StepperAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        StepperAddon.install()
    }
}
