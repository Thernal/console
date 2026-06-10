package io.thernal.console.stepper.ui

import io.thernal.console.api.autoinit.ConsoleAutoInitProvider
import io.thernal.console.stepper.ui.addon.StepperAddon

internal class StepperAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        StepperAddon.install()
    }
}
