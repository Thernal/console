package az.theternal.console.ui.designsystem.components.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import az.theternal.console.ui.designsystem.components.core.DsDivider
import az.theternal.console.ui.designsystem.components.core.DsIcon
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
fun DsBottomNavBar(
    selectedIndex: Int,
    items: List<DsBottomNavBarItem>,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.background1),
    ) {
        DsDivider(color = Theme.colors.border)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(navInsets)
                .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp8),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                val interactionSource = remember { MutableInteractionSource() }

                DsIcon(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onIndexChange(index) },
                        )
                        .padding(vertical = Theme.dimens.dp8),
                    icon = if (isSelected) item.selectedIcon else item.icon,
                    size = Theme.metrics.iconMd,
                    tint = if (isSelected) Theme.colors.primary02 else Theme.colors.content03,
                )
            }
        }
    }
}

data class DsBottomNavBarItem(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
)
