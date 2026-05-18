package az.theternal.console.ui.designsystem.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
fun DsCard(
    modifier: Modifier = Modifier,
    borderColor: Color = Theme.colors.border,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape = Theme.rounding.r12)
            .background(color = Theme.colors.background2)
            .border(
                width = Theme.metrics.borderWidth,
                color = borderColor,
                shape = Theme.rounding.r12,
            ),
    ) {
        content()
    }
}
