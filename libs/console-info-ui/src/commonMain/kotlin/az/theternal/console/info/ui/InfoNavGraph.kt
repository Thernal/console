package az.theternal.console.info.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import az.theternal.console.ui.base.NavGraph

internal object InfoNavGraph : NavGraph {
    override val title = "Info"
    override val icon: ImageVector = Icons.Default.Info

    @Composable
    override fun Content() {
        InfoScreen()
    }
}
