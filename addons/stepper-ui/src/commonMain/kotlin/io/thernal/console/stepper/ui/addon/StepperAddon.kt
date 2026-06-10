package io.thernal.console.stepper.ui.addon

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import io.thernal.console.stepper.ui.stepper.Stepper
import io.thernal.console.stepper.ui.view.overlay.StepperOverlayView
import io.thernal.console.runtime.console.ConsoleScope
import io.thernal.console.api.addon.ConsoleAddon
import io.thernal.console.api.addon.ConsoleNavGraph
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.stepper.ui.navigation.StepperNavGraph
import io.thernal.console.stepper.ui.navigation.StepperTab

object StepperAddon : ConsoleAddon {
    override fun onInstall(console: ConsoleScope) {
        console.addObserver(Stepper)
    }

    override fun tab(): ConsoleTab = StepperTab
    override fun navGraph(): ConsoleNavGraph = StepperNavGraph
    override fun overlay(): @Composable BoxScope.() -> Unit = { StepperOverlayView() }
}
