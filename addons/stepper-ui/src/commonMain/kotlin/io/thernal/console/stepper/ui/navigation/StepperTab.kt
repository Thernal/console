package io.thernal.console.stepper.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.stepper.ui.view.caught.StepperCaughtView
import io.thernal.console.stepper.ui.view.caught.components.StepperCaughtActions

internal object StepperTab : ConsoleTab {
    override val title: String = "Stepper"
    override val icon: ImageVector = Icons.Outlined.BugReport
    override val order = 10

    @Composable
    override fun Content() {
        StepperCaughtView()
    }

    @Composable
    override fun Actions() {
        StepperCaughtActions()
    }
}
