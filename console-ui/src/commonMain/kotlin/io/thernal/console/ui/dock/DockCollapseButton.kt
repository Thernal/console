package io.thernal.console.ui.dock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.foundation.theme.Theme

private val collapseSize = 32.dp

/** A button at the end of the header that folds the whole dock into a bubble. */
@Composable
internal fun DockCollapseButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(collapseSize)
            .clip(Theme.rounding.r8)
            .semantics { contentDescription = "Collapse floating widgets" }
            .pressable(onPress = onClick),
        contentAlignment = Alignment.Center,
    ) {
        DsIcon(icon = Icons.Default.CloseFullscreen, size = Theme.metrics.iconSm, color = Theme.colors.content04)
    }
}
