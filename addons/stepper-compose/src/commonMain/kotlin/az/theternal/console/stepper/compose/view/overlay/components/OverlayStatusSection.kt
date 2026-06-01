package az.theternal.console.stepper.compose.view.overlay.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun RowScope.OverlayEnabledSection(
    isEnabled: State<Boolean>,
    isExpanded: State<Boolean>,
    statusText: State<String>,
    caughtCount: State<Int>,
    badgeColor: State<Color>,
) {
    if (!isEnabled.value) return
    OverlayEnabledContent(
        isExpanded = isExpanded,
        statusText = statusText,
        caughtCount = caughtCount,
        badgeColor = badgeColor,
    )
}

@Composable
private fun RowScope.OverlayEnabledContent(
    isExpanded: State<Boolean>,
    statusText: State<String>,
    caughtCount: State<Int>,
    badgeColor: State<Color>,
) {
    if (isExpanded.value) {
        OverlayStatusText(statusText = statusText, modifier = Modifier.weight(1f))
    }
    OverlayCaughtBadge(caughtCount = caughtCount, color = badgeColor)
}

@Composable
internal fun OverlayStatusText(
    statusText: State<String>,
    modifier: Modifier = Modifier,
) {
    DsText(
        text = statusText.value,
        style = Theme.typography.body02,
        color = Theme.colors.content03,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.padding(start = Theme.dimens.dp8),
    )
}

@Composable
internal fun OverlayCaughtBadge(
    caughtCount: State<Int>,
    color: State<Color>,
) {
    if (caughtCount.value <= 0) return
    OverlayCaughtBadgeContent(caughtCount = caughtCount, color = color)
}

@Composable
private fun OverlayCaughtBadgeContent(
    caughtCount: State<Int>,
    color: State<Color>,
) {
    val resolvedColor = color.value
    Box(
        modifier = Modifier
            .clip(Theme.rounding.r4)
            .padding(start = Theme.dimens.dp8)
            .background(resolvedColor.copy(alpha = Theme.opacity.S15))
            .padding(horizontal = Theme.dimens.dp6, vertical = Theme.dimens.dp4),
    ) {
        DsText(
            text = "${caughtCount.value}",
            style = Theme.typography.label01,
            color = resolvedColor,
        )
    }
}
