package az.theternal.console.stepper.compose

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import az.theternal.console.stepper.DebugStepper
import az.theternal.console.stepper.compose.overlay.DebugStepperOverlay
import az.theternal.console.runtime.ConsoleScope
import az.theternal.console.api.ConsoleAddon
import az.theternal.console.api.ConsoleNavGraph

object DebugStepperAddon : ConsoleAddon {

    override fun onInstall(console: ConsoleScope) {
        console.addObserver(DebugStepper)
    }

    override fun navGraph(): ConsoleNavGraph {
        return DebugStepperNavGraph
    }

    override fun overlay(): @Composable BoxScope.() -> Unit {
        return { DebugStepperOverlay() }
    }
}
