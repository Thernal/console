package az.theternal.console.ui.designsystem.components.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import az.theternal.console.ui.designsystem.components.modifier.pressable
import az.theternal.console.ui.designsystem.foundation.theme.LocalDsContentColor
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
fun DsIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentColor: Color = Theme.colors.content01,
    content: @Composable () -> Unit,
) {
    val tint = if (enabled) {
        contentColor
    } else {
        Theme.colors.content03
    }

    CompositionLocalProvider(LocalDsContentColor provides tint) {
        Box(
            modifier = modifier
                .size(Theme.metrics.minTouchTarget)
                .clip(CircleShape)
                .pressable(
                    enabled = enabled,
                    onPress = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}
