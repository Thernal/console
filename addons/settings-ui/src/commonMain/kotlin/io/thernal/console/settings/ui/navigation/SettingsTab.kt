package io.thernal.console.settings.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.settings.ui.view.settings.SettingsView

internal object SettingsTab : ConsoleTab {
    override val title = "Settings"
    override val icon: ImageVector = Icons.Default.Settings
    override val order = Int.MAX_VALUE

    @Composable
    override fun Content() {
        SettingsView()
    }
}
