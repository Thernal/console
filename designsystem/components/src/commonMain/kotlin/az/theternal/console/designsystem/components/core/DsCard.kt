package az.theternal.console.designsystem.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme

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

@DsPreview
@Composable
private fun PreviewDsCard() {
    ThemeProvider {
        DsCard {
            DsText(
                text = "Card content",
                modifier = Modifier.padding(Theme.dimens.dp16),
            )
        }
    }
}
