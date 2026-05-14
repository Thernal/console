package az.theternal.console.debugstepper.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import az.theternal.console.debugstepper.ui.screen.DebugStepperScreen
import az.theternal.console.ui.nav.ConsoleTab

object DebugStepperNavGraph : ConsoleTab {
    override val title: String = "Stepper"
    override val icon: ImageVector = Icons.Outlined.BugReport

    @Composable
    override fun Content() {
        DebugStepperScreen()
    }

    override fun EntryProviderScope<NavKey>.routes() {}
}
