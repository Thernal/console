package az.theternal.console.debugstepper.ui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import az.theternal.console.debugstepper.DebugStepper
import az.theternal.console.debugstepper.ui.DebugStepperNavGraph
import az.theternal.console.ui.nav.ConsoleRoute
import az.theternal.console.ui.nav.LocalConsoleNavigator
import az.theternal.console.ui.renderer.LocalLogRenderer
import az.theternal.console.ui.designsystem.components.core.DsDivider
import az.theternal.console.ui.designsystem.components.core.DsIcon
import az.theternal.console.ui.designsystem.components.core.DsText
import az.theternal.console.ui.designsystem.foundation.theme.Theme
import kotlin.math.roundToInt

@Composable
internal fun DebugStepperFloatingCard(uiState: DebugStepperOverlayUiState) {
    val navigator = LocalConsoleNavigator.current
    val renderer = LocalLogRenderer.current
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var offsetX by rememberSaveable { mutableFloatStateOf(0f) }
    var offsetY by rememberSaveable { mutableFloatStateOf(0f) }

    val enabled = uiState.isEnabled
    val statusColor = overlayStatusColor(uiState.statusTone)
    val iconSize = Theme.metrics.iconMd
    val smallIconSize = Theme.dimens.dp18
    val iconTint = Theme.colors.content01
    val disabledTint = Theme.colors.content03
    val mutedTint = overlayMutedContentColor

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeContent),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                },
        ) {
            Column(
                modifier = Modifier
                    .clip(Theme.rounding.r12)
                    .background(overlayBackgroundColor)
                    .border(
                        width = Theme.metrics.borderWidth,
                        color = statusColor.copy(alpha = 0.4f),
                        shape = Theme.rounding.r12,
                    )
                    .padding(horizontal = Theme.dimens.dp10, vertical = Theme.dimens.dp8)
                    .then(if (enabled && isExpanded) Modifier.width(Theme.dimens.dp220) else Modifier),
            ) {
                when {
                    !enabled -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
                        ) {
                            DsIcon(
                                icon = Icons.Outlined.PowerSettingsNew,
                                size = iconSize,
                                modifier = Modifier.clickable { DebugStepper.updateConfig { copy(enabled = true) } },
                                tint = disabledTint,
                            )
                            ExpandIcon(
                                isExpanded = isExpanded,
                                size = smallIconSize,
                                tint = mutedTint,
                                onClick = { isExpanded = !isExpanded },
                            )
                        }
                    }

                    !isExpanded -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(Theme.metrics.statusDotSize)
                                    .clip(CircleShape)
                                    .background(statusColor),
                            )
                            DsIcon(
                                icon = Icons.Outlined.PowerSettingsNew,
                                size = iconSize,
                                modifier = Modifier.clickable { DebugStepper.updateConfig { copy(enabled = false) } },
                                tint = iconTint,
                            )
                            DsIcon(
                                icon = if (uiState.isPaused) {
                                    Icons.Outlined.PlayArrow
                                } else {
                                    Icons.Outlined.Pause
                                },
                                size = iconSize,
                                modifier = Modifier.clickable { DebugStepper.updateConfig { copy(paused = !paused) } },
                                tint = iconTint,
                            )
                            DsIcon(
                                icon = Icons.Outlined.SkipNext,
                                size = iconSize,
                                modifier = Modifier.clickable { DebugStepper.next() },
                                tint = if (uiState.canStep) iconTint else disabledTint,
                            )
                            DsIcon(
                                icon = Icons.Outlined.Settings,
                                size = smallIconSize,
                                tint = mutedTint,
                                modifier = Modifier.clickable { navigator.openTab(DebugStepperNavGraph) },
                            )
                            ExpandIcon(
                                isExpanded = false,
                                size = smallIconSize,
                                tint = mutedTint,
                                onClick = { isExpanded = true },
                            )
                        }
                    }

                    else -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(Theme.metrics.statusDotSize)
                                    .clip(CircleShape)
                                    .background(statusColor),
                            )
                            DsText(
                                text = uiState.displayTag ?: "Stepper",
                                color = iconTint,
                                style = Theme.typography.body03,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                            DsText(
                                text = uiState.statusText,
                                color = mutedTint,
                                style = Theme.typography.label02,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            DsIcon(
                                icon = Icons.Outlined.PowerSettingsNew,
                                size = iconSize,
                                modifier = Modifier.clickable { DebugStepper.updateConfig { copy(enabled = false) } },
                                tint = iconTint,
                            )
                            DsIcon(
                                icon = if (uiState.isPaused) {
                                    Icons.Outlined.PlayArrow
                                } else {
                                    Icons.Outlined.Pause
                                },
                                size = iconSize,
                                modifier = Modifier.clickable { DebugStepper.updateConfig { copy(paused = !paused) } },
                                tint = iconTint,
                            )
                            DsIcon(
                                icon = Icons.Outlined.SkipNext,
                                size = iconSize,
                                modifier = Modifier.clickable { DebugStepper.next() },
                                tint = if (uiState.canStep) iconTint else disabledTint,
                            )
                            DsIcon(
                                icon = Icons.Outlined.Settings,
                                size = smallIconSize,
                                tint = mutedTint,
                                modifier = Modifier.clickable { navigator.openTab(DebugStepperNavGraph) },
                            )
                            ExpandIcon(
                                isExpanded = true,
                                size = smallIconSize,
                                tint = mutedTint,
                                onClick = { isExpanded = false },
                            )
                        }

                        DsDivider(
                            modifier = Modifier.padding(vertical = Theme.dimens.dp6),
                            color = overlayDividerColor,
                        )
                        if (uiState.currentLog != null) {
                            renderer.Item(
                                log = uiState.currentLog,
                                onClick = {
                                    navigator.push(ConsoleRoute.LogDetail("", uiState.currentLog.id))
                                },
                            )
                        } else {
                            DsText(
                                text = "Nothing caught yet.",
                                color = mutedTint,
                                style = Theme.typography.label01,
                                modifier = Modifier.padding(vertical = Theme.dimens.dp4),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandIcon(
    isExpanded: Boolean,
    size: Dp,
    tint: Color,
    onClick: () -> Unit,
) {
    DsIcon(
        icon = if (isExpanded) {
            Icons.Outlined.KeyboardArrowUp
        } else {
            Icons.Outlined.KeyboardArrowDown
        },
        size = size,
        tint = tint,
        modifier = Modifier.clickable(onClick = onClick),
    )
}
