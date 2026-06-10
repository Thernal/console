package io.thernal.console.logging.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.logging.ui.view.logs.LogsView

internal object LogsTab : ConsoleTab {
    override val title = "Logs"
    override val icon: ImageVector = Icons.Default.Menu
    override val order = 0

    @Composable
    override fun Content() {
        LogsView()
    }
}
