package io.thernal.console.network.ui.view.networklogdetail.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.core.chip.DsChipSize
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.foundation.theme.Theme
import kotlinx.coroutines.delay

@Composable
internal fun CopyChip(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
) {
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(1500L)
            copied = false
        }
    }

    val color = if (copied) {
        Theme.colors.success
    } else {
        accentColor
    }

    DsChip(
        label = label,
        selected = copied,
        color = color,
        size = DsChipSize.Small,
        leading = {
            DsIcon(
                icon = if (copied) {
                    Icons.Outlined.Check
                } else {
                    icon
                },
                size = Theme.metrics.iconXs,
                color = color,
                modifier = Modifier.padding(end = Theme.dimens.dp4),
            )
        },
        modifier = Modifier.pressable(onPress = {
            copied = true
            onClick()
        }),
    )
}
