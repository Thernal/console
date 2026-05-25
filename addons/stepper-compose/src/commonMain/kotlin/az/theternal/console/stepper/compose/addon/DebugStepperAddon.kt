package az.theternal.console.stepper.compose.addon

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import az.theternal.console.stepper.DebugStepper
import az.theternal.console.stepper.compose.view.overlay.DebugStepperOverlay
import az.theternal.console.runtime.ConsoleScope
import az.theternal.console.api.addon.ConsoleAddon
import az.theternal.console.api.addon.ConsoleNavGraph
import az.theternal.console.api.addon.ConsoleTab
import az.theternal.console.stepper.compose.navigation.DebugStepperNavGraph
import az.theternal.console.stepper.compose.navigation.DebugStepperTab

object DebugStepperAddon : ConsoleAddon {
    override fun onInstall(console: ConsoleScope) {
        console.addObserver(DebugStepper)
    }

    override fun tab(): ConsoleTab = DebugStepperTab
    override fun navGraph(): ConsoleNavGraph = DebugStepperNavGraph
    override fun overlay(): @Composable BoxScope.() -> Unit = { DebugStepperOverlay() }
}
