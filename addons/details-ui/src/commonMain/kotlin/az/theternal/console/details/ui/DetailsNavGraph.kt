package az.theternal.console.details.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import az.theternal.console.addon.api.nav.ConsoleTab

internal object DetailsNavGraph : ConsoleTab {
    override val title = "Details"
    override val icon: ImageVector = Icons.Default.Info

    @Composable
    override fun Content() {
        DetailsScreen()
    }
}
