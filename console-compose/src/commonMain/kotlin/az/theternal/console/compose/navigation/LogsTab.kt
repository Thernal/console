package az.theternal.console.compose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import az.theternal.console.api.addon.ConsoleTab
import az.theternal.console.compose.view.logs.LogsView

internal object LogsTab : ConsoleTab {
    override val title = "Logs"
    override val icon: ImageVector = Icons.Default.Menu
    override val order = 0

    @Composable
    override fun Content() {
        LogsView()
    }
}
