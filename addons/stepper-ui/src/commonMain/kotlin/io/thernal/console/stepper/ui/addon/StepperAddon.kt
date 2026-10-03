@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.stepper.ui.addon

import io.thernal.console.stepper.ui.stepper.Stepper
import io.thernal.console.stepper.ui.dock.StepperDockWidget
import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.runtime.console.Console
import io.thernal.console.api.addon.ConsoleAddon
import io.thernal.console.api.addon.ConsoleDockWidget
import io.thernal.console.api.addon.ConsoleNavGraph
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.stepper.ui.navigation.StepperNavGraph
import io.thernal.console.stepper.ui.navigation.StepperTab
import io.thernal.console.settings.SettingsRegistry

object StepperAddon : ConsoleAddon {
    override fun onInstall() {
        Console.addObserver(Stepper)
        SettingsRegistry.register(stepperSettingsSection())
    }

    override fun tab(): ConsoleTab = StepperTab
    override fun navGraph(): ConsoleNavGraph = StepperNavGraph
    override fun dockWidget(): ConsoleDockWidget = StepperDockWidget
}
