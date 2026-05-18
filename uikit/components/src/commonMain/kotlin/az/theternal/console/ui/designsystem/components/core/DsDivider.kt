package az.theternal.console.ui.designsystem.components.core

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
fun DsDivider(
    modifier: Modifier = Modifier,
    color: Color = Theme.colors.border,
    thickness: Dp = Theme.metrics.dividerHeight,
) {
    Canvas(modifier.fillMaxWidth().height(thickness)) {
        drawLine(
            color = color,
            strokeWidth = thickness.toPx(),
            start = Offset(x = 0f, y = thickness.toPx() / 2),
            end = Offset(x = size.width, y = thickness.toPx() / 2),
        )
    }
}
