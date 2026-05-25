package az.theternal.console.details.compose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import az.theternal.console.api.addon.ConsoleTab
import az.theternal.console.details.compose.view.details.DetailsView

internal object DetailsTab : ConsoleTab {
    override val title = "Details"
    override val icon: ImageVector = Icons.Default.Info

    @Composable
    override fun Content() {
        DetailsView()
    }
}
