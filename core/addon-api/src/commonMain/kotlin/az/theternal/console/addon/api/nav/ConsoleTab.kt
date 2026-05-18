package az.theternal.console.addon.api.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

interface ConsoleTab : ConsoleNavGraph {
    val title: String
    val icon: ImageVector
    val order: Int get() = Int.MAX_VALUE

    @Composable
    fun Content()
}
