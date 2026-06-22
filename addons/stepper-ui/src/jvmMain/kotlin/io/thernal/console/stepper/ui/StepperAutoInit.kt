package io.thernal.console.stepper.ui

import io.thernal.console.api.autoinit.ConsoleInitializer
import io.thernal.console.stepper.ui.addon.StepperAddon

internal class StepperAutoInit : ConsoleInitializer {
    override fun init() {
        StepperAddon.install()
    }
}
