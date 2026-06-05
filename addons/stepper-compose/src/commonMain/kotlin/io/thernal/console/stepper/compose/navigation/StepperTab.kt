package io.thernal.console.stepper.compose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.stepper.compose.view.settings.StepperSettingsView

internal object StepperTab : ConsoleTab {
    override val title: String = "Stepper"
    override val icon: ImageVector = Icons.Outlined.BugReport

    @Composable
    override fun Content() {
        StepperSettingsView()
    }
}
