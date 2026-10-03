package io.thernal.console.ui.dock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import io.thernal.console.api.addon.ConsoleDockWidget
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.foundation.theme.Theme

private const val TAB_ANIMATION_MS = 180

/** One addon's tab: its icon and name, tinted when selected, with a status dot on the icon. */
@Composable
internal fun DockTab(
    widget: ConsoleDockWidget,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val fill by animateColorAsState(
        targetValue = if (isSelected) Theme.colors.primary01.copy(alpha = Theme.opacity.S12) else Color.Transparent,
        animationSpec = tween(TAB_ANIMATION_MS),
        label = "dock-tab-fill",
    )
    val tint by animateColorAsState(
        targetValue = if (isSelected) Theme.colors.primary01 else Theme.colors.content03,
        animationSpec = tween(TAB_ANIMATION_MS),
        label = "dock-tab-tint",
    )
    Row(
        modifier = Modifier
            .clip(Theme.rounding.r8)
            .background(fill)
            .semantics { contentDescription = widget.title }
            .pressable(onPress = onClick)
            .padding(horizontal = Theme.dimens.dp10, vertical = Theme.dimens.dp6),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            DsIcon(icon = widget.icon, size = Theme.metrics.iconSm, color = tint)
            DockStatusDot(status = widget.status(), modifier = Modifier.align(Alignment.TopEnd))
        }
        AnimatedVisibility(
            visible = isSelected,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut(),
        ) {
            DsText(
                text = widget.title,
                style = Theme.typography.label01,
                color = tint,
                modifier = Modifier.padding(start = Theme.dimens.dp6),
            )
        }
    }
}
