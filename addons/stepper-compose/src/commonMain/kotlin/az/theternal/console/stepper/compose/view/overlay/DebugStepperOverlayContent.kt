package az.theternal.console.stepper.compose.view.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import az.theternal.console.api.navigation.ConsoleRoute
import az.theternal.console.api.navigation.LocalConsoleNavigator
import az.theternal.console.api.ui.LocalLogRenderer
import az.theternal.console.compose.core.preview
import az.theternal.console.compose.util.logAccentColor
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.components.core.DsDivider
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.foundation.theme.Theme
import az.theternal.console.stepper.compose.navigation.DebugStepperTab
import az.theternal.console.stepper.compose.view.overlay.model.DebugStepperOverlayIntent
import az.theternal.console.stepper.compose.view.overlay.model.DebugStepperOverlayState
import az.theternal.console.stepper.compose.view.overlay.components.OverlayIconButton
import az.theternal.console.stepper.compose.view.overlay.components.OverlayIconControls
import kotlin.math.roundToInt

@Composable
internal fun DebugStepperOverlayContent(
    state: DebugStepperOverlayState,
    dispatch: (DebugStepperOverlayIntent) -> Unit,
) {
    val navigator = LocalConsoleNavigator.current
    val renderer = LocalLogRenderer.current
    val drag = rememberDragOffset()

    val statusColor = overlayStatusColor(state.statusTone.value)
    val borderColor = state.currentLog.value?.logAccentColor() ?: statusColor

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
                .offset { IntOffset(drag.offsetX.roundToInt(), drag.offsetY.roundToInt()) }
                .widthIn(max = maxWidth)
                .clip(Theme.rounding.r12)
                .background(overlayBackgroundColor)
                .border(width = Theme.dimens.dp2, color = borderColor, shape = Theme.rounding.r12)
                .onSizeChanged { newSize -> drag.onSizeChanged(newSize, maxWidthPx, maxHeightPx) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        drag.onDrag(dragAmount, maxWidthPx, maxHeightPx)
                    }
                },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = Theme.dimens.dp4, vertical = Theme.dimens.dp3),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4),
            ) {
                if (state.isEnabled.value) {
                    if (state.isExpanded.value) {
                        DsText(
                            text = state.statusText.value,
                            style = Theme.typography.body02,
                            color = Theme.colors.content03,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f).padding(start = Theme.dimens.dp8),
                        )
                    }

                    if (state.caughtCount.value > 0) {
                        Box(
                            modifier = Modifier
                                .clip(Theme.rounding.r4)
                                .padding(start = Theme.dimens.dp8)
                                .background(borderColor.copy(alpha = Theme.opacity.S15))
                                .padding(horizontal = Theme.dimens.dp6, vertical = Theme.dimens.dp4),
                        ) {
                            DsText(
                                text = "${state.caughtCount.value}",
                                style = Theme.typography.label01,
                                color = borderColor,
                            )
                        }
                    }
                }

                OverlayIconControls(
                    state = state,
                    dispatch = dispatch,
                )

                OverlayIconButton(onClick = { navigator.openTab(DebugStepperTab) }) {
                    DsIcon(
                        icon = Icons.Outlined.Settings,
                        size = Theme.metrics.iconSm,
                        color = overlayMutedContentColor,
                    )
                }

                if (state.isEnabled.value) {
                    OverlayIconButton(
                        onClick = { dispatch(DebugStepperOverlayIntent.ToggleExpanded) },
                    ) {
                        DsIcon(
                            icon = if (state.isExpanded.value) {
                                Icons.Outlined.KeyboardArrowDown
                            } else {
                                Icons.Outlined.KeyboardArrowUp
                            },
                            size = Theme.metrics.iconMd,
                            color = overlayMutedContentColor,
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = state.isExpanded.value && state.currentLog.value != null,
                enter = expandVertically(tween(200), expandFrom = Alignment.Top) + fadeIn(tween(150)),
                exit = shrinkVertically(tween(150), shrinkTowards = Alignment.Top) + fadeOut(tween(100)),
            ) {
                state.currentLog.value?.let { log ->
                    Column(modifier = Modifier.padding(horizontal = Theme.dimens.dp12)) {
                        DsDivider(color = overlayDividerColor)
                        renderer.Item(
                            log = log,
                            onClick = { navigator.push(ConsoleRoute.LogDetail("", log.id)) },
                        )
                        Spacer(Modifier.height(Theme.dimens.dp8))
                    }
                }
            }
        }
    }
}

@DsPreview
@Composable
private fun PreviewDebugStepperOverlayContentDisabled() {
    ThemeProvider {
        DebugStepperOverlayContent(state = DebugStepperOverlayState(), dispatch = {})
    }
}

@DsPreview
@Composable
private fun PreviewDebugStepperOverlayContentEnabled() {
    val state = DebugStepperOverlayState()
    state.isEnabled.preview(true)
    state.isPaused.preview(true)
    state.caughtCount.preview(3)
    state.isExpanded.preview(true)
    ThemeProvider {
        DebugStepperOverlayContent(state = state, dispatch = {})
    }
}
