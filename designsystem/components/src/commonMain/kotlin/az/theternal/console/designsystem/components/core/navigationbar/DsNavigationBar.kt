package az.theternal.console.designsystem.components.core.navigationbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Search
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
fun DsNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = Theme.colors.background2,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .windowInsetsPadding(
                insets = WindowInsets.safeDrawing.only(
                    sides = WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal,
                ),
            )
            .padding(vertical = Theme.dimens.dp4),
        content = content,
    )
}

@DsPreview
@Composable
private fun PreviewDsNavigationBar() {
    ThemeProvider {
        DsNavigationBar {
            DsNavigationBarItem(
                selected = true,
                onClick = {},
                icon = { DsIcon(icon = Icons.AutoMirrored.Outlined.List) },
                label = { DsText(text = "Logs") },
            )
            DsNavigationBarItem(
                selected = false,
                onClick = {},
                icon = { DsIcon(icon = Icons.Outlined.Search) },
                label = { DsText(text = "Search") },
            )
        }
    }
}
