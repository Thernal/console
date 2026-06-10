package io.thernal.console.details.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.details.ui.view.details.DetailsView

internal object DetailsTab : ConsoleTab {
    override val title = "Details"
    override val icon: ImageVector = Icons.Default.Info

    @Composable
    override fun Content() {
        DetailsView()
    }
}
