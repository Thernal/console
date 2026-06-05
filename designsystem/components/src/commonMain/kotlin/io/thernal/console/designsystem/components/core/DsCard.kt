package io.thernal.console.designsystem.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
fun DsCard(
    modifier: Modifier = Modifier,
    lineWidth: Dp = Theme.dimens.dp3,
    color: Color = Theme.colors.primary01,
    content: @Composable RowScope.() -> Unit,
) {
    DsContainer(
        modifier = modifier
            .fillMaxWidth(),
        borderColor = color.copy(alpha = Theme.opacity.S35),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Box(
                modifier = Modifier
                    .width(lineWidth)
                    .fillMaxHeight()
                    .background(color),
            )

            content()
        }
    }
}

@Composable
@DsPreview
private fun PreviewDsCard() {
    ThemeProvider {
        DsCard(
            color = Theme.colors.primary01,
        ) {
            DsText(
                modifier = Modifier.padding(Theme.dimens.dp8),
                text = "Card Content",
            )
        }
    }
}
