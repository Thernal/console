package az.theternal.console.debugstepper.ui.overlay.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import az.theternal.console.designsystem.components.modifier.pressable
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun OverlayIconButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(Theme.dimens.dp32)
            .clip(Theme.rounding.r8)
            .pressable(
                enabled = enabled,
                onPress = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
