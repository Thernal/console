package io.thernal.console.api.addon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector

@Stable
interface ConsoleTab {
    val title: String
    val icon: ImageVector
    val order: Int get() = Int.MAX_VALUE

    @Composable
    fun Content()
}
