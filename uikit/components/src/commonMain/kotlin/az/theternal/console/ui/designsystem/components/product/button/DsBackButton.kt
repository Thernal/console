package az.theternal.console.ui.designsystem.components.product.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import az.theternal.console.ui.designsystem.components.core.DsIcon
import az.theternal.console.ui.designsystem.components.core.DsText
import az.theternal.console.ui.designsystem.components.product.DsIconButton
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
fun DsBackButton(
    onBackClick: () -> Unit,
    headerText: String = "Back",
    icon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
) {
    DsIconButton(
        onTap = onBackClick,
        spacing = Theme.dimens.dp16,
        label = {
            DsText(
                text = headerText,
                style = Theme.typography.title01,
                color = Theme.colors.content01,
            )
        },
    ) {
        DsIcon(
            icon = icon,
            size = Theme.metrics.iconMd,
            tint = Theme.colors.content01,
        )
    }
}
