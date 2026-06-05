package io.thernal.console.stepper.compose.view.overlay.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.compose.common.logAccentColor
import io.thernal.console.compose.core.select
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.stepper.compose.navigation.StepperTab
import io.thernal.console.stepper.compose.stepper.StepperIntent
import io.thernal.console.stepper.compose.view.overlay.model.StepperStatusTone
import io.thernal.console.stepper.compose.view.overlay.model.StepperOverlayIntent
import io.thernal.console.stepper.compose.view.overlay.model.StepperOverlayState
import kotlin.math.roundToInt

@Composable
internal fun OverlayCard(
    state: StepperOverlayState,
    maxWidth: Dp,
    maxWidthPx: Float,
    maxHeightPx: Float,
    modifier: Modifier = Modifier,
    dispatch: (StepperOverlayIntent) -> Unit,
    onStepperDispatch: (StepperIntent) -> Unit,
) {
    val navigator = LocalConsoleNavigator.current

    val themeColors = Theme.colors
    val statusColor = state.statusTone.select {
        when (it) {
            StepperStatusTone.Disabled -> themeColors.content03
            StepperStatusTone.Paused -> themeColors.warning
            StepperStatusTone.Idle -> themeColors.info
            StepperStatusTone.Running -> themeColors.success
        }
    }

    val density = LocalDensity.current
    val borderStrokePx = with(density) { 2.dp.toPx() }
    val cornerRadiusPx = with(density) { 12.dp.toPx() }

    val accentColor: State<Color> = remember {
        derivedStateOf {
            val log = state.currentLog.value
            log?.logAccentColor(themeColors) ?: statusColor.value
        }
    }

    Column(
        modifier = modifier
            .offset { IntOffset(state.offsetX.value.roundToInt(), state.offsetY.value.roundToInt()) }
            .widthIn(max = maxWidth)
            .clip(Theme.rounding.r12)
            .background(Theme.colors.background1)
            .drawWithContent {
                drawContent()
                val color = accentColor.value
                val inset = borderStrokePx / 2f
                drawRoundRect(
                    color = color,
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - borderStrokePx, size.height - borderStrokePx),
                    style = Stroke(width = borderStrokePx),
                    cornerRadius = CornerRadius(cornerRadiusPx - inset),
                )
            }
            .onSizeChanged { newSize ->
                dispatch(
                    StepperOverlayIntent.CardSizeChanged(
                        newSize = newSize,
                        maxWidthPx = maxWidthPx,
                        maxHeightPx = maxHeightPx,
                    ),
                )
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    dispatch(
                        StepperOverlayIntent.Dragged(
                            dragAmount = dragAmount,
                            maxWidthPx = maxWidthPx,
                            maxHeightPx = maxHeightPx,
                        ),
                    )
                }
            },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Theme.dimens.dp4, vertical = Theme.dimens.dp3),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4),
        ) {
            OverlayEnabledSection(
                isEnabled = state.isEnabled,
                isExpanded = state.isExpanded,
                statusText = state.statusText,
                caughtCount = state.caughtCount,
                badgeColor = accentColor,
            )
            OverlayIconControls(
                isEnabled = state.isEnabled,
                isPaused = state.isPaused,
                canStep = state.canStep,
                dispatch = onStepperDispatch,
            )
            OverlayIconButton(
                onClick = { navigator.openTab(StepperTab) },
            ) {
                DsIcon(
                    icon = Icons.Outlined.Settings,
                    size = Theme.metrics.iconSm,
                    color = Theme.colors.content04,
                )
            }
            OverlayExpandSection(
                isEnabled = state.isEnabled,
                isExpanded = state.isExpanded,
                dispatch = dispatch,
            )
        }
        OverlayCurrentLogSection(
            isExpanded = state.isExpanded,
            currentLog = state.currentLog,
        )
    }
}
