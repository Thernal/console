package az.theternal.console.stepper.compose.addon

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import az.theternal.console.stepper.Stepper
import az.theternal.console.stepper.compose.view.overlay.StepperOverlay
import az.theternal.console.runtime.ConsoleScope
import az.theternal.console.api.addon.ConsoleAddon
import az.theternal.console.api.addon.ConsoleNavGraph
import az.theternal.console.api.addon.ConsoleTab
import az.theternal.console.stepper.compose.navigation.StepperNavGraph
import az.theternal.console.stepper.compose.navigation.StepperTab

object StepperAddon : ConsoleAddon {
    override fun onInstall(console: ConsoleScope) {
        console.addObserver(Stepper)
    }

    override fun tab(): ConsoleTab = StepperTab
    override fun navGraph(): ConsoleNavGraph = StepperNavGraph
    override fun overlay(): @Composable BoxScope.() -> Unit = { StepperOverlay() }
}
