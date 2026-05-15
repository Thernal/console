package az.theternal.console.debugstepper.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import az.theternal.console.debugstepper.DebugStepper
import az.theternal.console.debugstepper.ui.DebugStepperNavGraph
import az.theternal.console.ui.designsystem.components.provider.ThemeProvider
import az.theternal.console.ui.designsystem.foundation.theme.DsPreview
import az.theternal.console.ui.designsystem.components.core.DsDivider
import az.theternal.console.ui.utils.logAccentColor
import az.theternal.console.ui.designsystem.components.core.DsIcon
import az.theternal.console.ui.designsystem.components.core.DsText
import az.theternal.console.ui.designsystem.foundation.theme.Theme
import az.theternal.console.ui.nav.ConsoleRoute
import az.theternal.console.ui.nav.LocalConsoleNavigator
import az.theternal.console.ui.renderer.LocalLogRenderer
import kotlin.math.roundToInt

@Composable
internal fun DebugStepperFloatingCard(uiState: DebugStepperOverlayUiState) {
    val navigator = LocalConsoleNavigator.current
    val renderer = LocalLogRenderer.current
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var offsetX by rememberSaveable { mutableFloatStateOf(0f) }
    var offsetY by rememberSaveable { mutableFloatStateOf(0f) }
    var cardSize by remember { mutableStateOf(IntSize.Zero) }

    val statusColor = overlayStatusColor(uiState.statusTone)
    val borderColor = uiState.currentLog?.logAccentColor() ?: statusColor

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeContent),
    ) {
        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = constraints.maxHeight.toFloat()

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .widthIn(max = maxWidth)
                .clip(Theme.rounding.r12)
                .background(overlayBackgroundColor)
                .border(
                    width = Theme.dimens.dp2,
                    color = borderColor,
                    shape = Theme.rounding.r12,
                )
                .onSizeChanged { cardSize = it }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX = (offsetX + dragAmount.x).coerceIn(
                            minimumValue = -(maxWidthPx - cardSize.width).coerceAtLeast(0f),
                            maximumValue = 0f,
                        )
                        offsetY = (offsetY + dragAmount.y).coerceIn(
                            minimumValue = 0f,
                            maximumValue = (maxHeightPx - cardSize.height).coerceAtLeast(0f),
                        )
                    }
                },
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = Theme.dimens.dp8,
                    vertical = Theme.dimens.dp8,
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
            ) {
                if (isExpanded) {
                    DsText(
                        text = uiState.statusText,
                        style = Theme.typography.body02,
                        color = Theme.colors.content03,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                }

                if (uiState.caughtCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(Theme.rounding.r4)
                            .background(borderColor.copy(alpha = 0.15f))
                            .padding(horizontal = Theme.dimens.dp6, vertical = Theme.dimens.dp2),
                    ) {
                        DsText(
                            text = "${uiState.caughtCount}",
                            style = Theme.typography.label01,
                            color = borderColor,
                        )
                    }
                }

                OverlayIconControls(
                    isEnabled = uiState.isEnabled,
                    isPaused = uiState.isPaused,
                    canStep = uiState.canStep,
                )

                OverlayIconButton(onClick = { navigator.openTab(DebugStepperNavGraph) }) {
                    DsIcon(
                        icon = Icons.Outlined.Settings,
                        size = Theme.metrics.iconSm,
                        tint = overlayMutedContentColor,
                    )
                }

                OverlayIconButton(onClick = { isExpanded = !isExpanded }) {
                    DsIcon(
                        icon = if (isExpanded) Icons.Outlined.KeyboardArrowDown else Icons.Outlined.KeyboardArrowUp,
                        size = Theme.metrics.iconMd,
                        tint = overlayMutedContentColor,
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded && uiState.currentLog != null,
                enter = expandVertically(tween(200), expandFrom = Alignment.Top) + fadeIn(tween(150)),
                exit = shrinkVertically(tween(150), shrinkTowards = Alignment.Top) + fadeOut(tween(100)),
            ) {
                uiState.currentLog?.let { currentLog ->
                    Column {
                        DsDivider(color = overlayDividerColor)
                        renderer.Item(
                            log = currentLog,
                            onClick = {
                                navigator.push(ConsoleRoute.LogDetail("", currentLog.id))
                            },
                        )
                        Spacer(Modifier.height(Theme.dimens.dp8))
                    }
                }
            }
        }
    }
}

@Composable
private fun OverlayIconControls(
    isEnabled: Boolean,
    isPaused: Boolean,
    canStep: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OverlayIconButton(
            onClick = { DebugStepper.updateConfig { copy(enabled = !isEnabled) } },
        ) {
            DsIcon(
                icon = Icons.Outlined.PowerSettingsNew,
                size = Theme.metrics.iconMd,
                tint = if (isEnabled) Theme.colors.content01 else overlayMutedContentColor,
            )
        }

        if (isEnabled) {
            OverlayIconButton(
                onClick = { DebugStepper.updateConfig { copy(paused = !isPaused) } },
            ) {
                DsIcon(
                    icon = if (isPaused) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
                    size = Theme.metrics.iconMd,
                    tint = Theme.colors.content01,
                )
            }

            OverlayIconButton(
                enabled = canStep,
                onClick = { DebugStepper.next() },
            ) {
                DsIcon(
                    icon = Icons.Outlined.SkipNext,
                    size = Theme.metrics.iconMd,
                    tint = if (canStep) Theme.colors.content01 else overlayMutedContentColor,
                )
            }
        }
    }
}

@Composable
private fun OverlayIconButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(Theme.dimens.dp32)
            .clip(Theme.rounding.r8)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@DsPreview
@Composable
private fun PreviewOverlayControls() {
    ThemeProvider {
        Column(
            modifier = Modifier
                .background(overlayBackgroundColor)
                .padding(Theme.dimens.dp8),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
        ) {
            OverlayIconControls(isEnabled = true, isPaused = true, canStep = true)
            OverlayIconControls(isEnabled = true, isPaused = false, canStep = false)
            OverlayIconControls(isEnabled = false, isPaused = false, canStep = false)
        }
    }
}
