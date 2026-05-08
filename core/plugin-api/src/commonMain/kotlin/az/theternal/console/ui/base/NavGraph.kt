package az.theternal.console.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

interface NavGraph {
    val title: String
    val icon: ImageVector

    @Composable
    fun Content()

    fun EntryProviderScope<NavKey>.routes() {}
}
