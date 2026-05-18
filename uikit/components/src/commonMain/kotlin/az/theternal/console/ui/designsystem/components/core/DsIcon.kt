package az.theternal.console.ui.designsystem.components.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toolingGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import az.theternal.console.ui.designsystem.foundation.theme.LocalDsContentColor
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
fun DsIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = LocalDsContentColor.current,
    size: Dp = Theme.metrics.iconMd,
) {
    val colorFilter = remember(color) {
        if (color == Color.Unspecified) {
            null
        } else {
            ColorFilter.tint(color)
        }
    }

    Box(
        modifier.toolingGraphicsLayer()
            .paint(
                painter = rememberVectorPainter(icon),
                colorFilter = colorFilter,
                contentScale = ContentScale.Fit,
            )
            .size(size),
    )
}
