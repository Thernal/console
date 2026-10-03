package io.thernal.console.inspector.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.inspector.ui.view.inspector.InspectorActions
import io.thernal.console.inspector.ui.view.inspector.InspectorView

internal object InspectorTab : ConsoleTab {
    override val title = "Inspector"
    override val icon: ImageVector = Icons.Default.AccountTree
    override val order = 40

    @Composable
    override fun Content() {
        InspectorView()
    }

    @Composable
    override fun Actions() {
        InspectorActions()
    }
}
