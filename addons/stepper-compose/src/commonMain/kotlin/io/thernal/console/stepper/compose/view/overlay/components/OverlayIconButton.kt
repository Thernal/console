package io.thernal.console.stepper.compose.view.overlay.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun OverlayIconButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(Theme.dimens.dp40)
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
