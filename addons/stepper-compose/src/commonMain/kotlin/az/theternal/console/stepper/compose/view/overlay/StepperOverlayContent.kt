package az.theternal.console.stepper.compose.view.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import az.theternal.console.compose.util.themeColor
import az.theternal.console.compose.core.preview
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme
import az.theternal.console.runtime.LogLevel
import az.theternal.console.stepper.compose.navigation.StepperTab
import az.theternal.console.stepper.compose.view.overlay.components.OverlayCurrentLogSection
import az.theternal.console.stepper.compose.view.overlay.components.OverlayEnabledSection
import az.theternal.console.stepper.compose.view.overlay.components.OverlayExpandSection
import az.theternal.console.stepper.compose.view.overlay.components.OverlayIconButton
import az.theternal.console.stepper.compose.view.overlay.components.OverlayIconControls
import az.theternal.console.stepper.compose.view.overlay.model.DragOffsetState
import az.theternal.console.stepper.compose.view.overlay.model.StepperOverlayIntent
import az.theternal.console.stepper.compose.view.overlay.model.StepperOverlayState
import az.theternal.console.stepper.compose.view.overlay.model.rememberDragOffset
import az.theternal.console.api.navigation.LocalConsoleNavigator
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
internal fun StepperOverlayContent(
    state: StepperOverlayState,
    dispatch: (StepperOverlayIntent) -> Unit,
) {
    val drag = rememberDragOffset()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeContent),
    ) {
        OverlayCard(
            state = state,
            drag = drag,
            maxWidth = maxWidth,
            maxWidthPx = constraints.maxWidth.toFloat(),
            maxHeightPx = constraints.maxHeight.toFloat(),
            modifier = Modifier.align(Alignment.TopEnd),
            dispatch = dispatch,
        )
    }
}

@Composable
private fun OverlayCard(
    state: StepperOverlayState,
    drag: DragOffsetState,
    maxWidth: Dp,
    maxWidthPx: Float,
    maxHeightPx: Float,
    modifier: Modifier = Modifier,
    dispatch: (StepperOverlayIntent) -> Unit,
) {
    val navigator = LocalConsoleNavigator.current

    // Precompute theme-dependent values in composable scope.
    // statusColor is @Composable, so it must stay here; rememberUpdatedState ensures
    // the derivedStateOf below always sees the latest color without an extra recompose trigger.
    val themeColors = Theme.colors
    val statusColor = when (state.statusTone.value) {
        StepperStatusTone.Disabled -> Theme.colors.content03
        StepperStatusTone.Paused -> Theme.colors.warning
        StepperStatusTone.Idle -> Theme.colors.info
        StepperStatusTone.Running -> Theme.colors.success
    }
    val themeColorsState = rememberUpdatedState(themeColors)
    val statusColorState = rememberUpdatedState(statusColor)

    // Precompute pixel sizes once – avoids any Density lookup inside the draw lambda.
    val density = LocalDensity.current
    val borderStrokePx = with(density) { 2.dp.toPx() }
    val cornerRadiusPx = with(density) { 12.dp.toPx() }

    // borderColorState is a derivedStateOf that reads currentLog only in the snapshot
    // context, NOT in composition. When currentLog changes the border is repainted in the
    // draw phase only – OverlayCard itself does not recompose for every log event.
    val borderColorState: State<Color> = remember {
        derivedStateOf {
            val log = state.currentLog.value
            if (log != null) {
                val colors = themeColorsState.value
                if (log.level == LogLevel.None) {
                    val palette = listOf(
                        colors.primary01,
                        colors.success,
                        colors.warning,
                        colors.info,
                        colors.danger,
                    )
                    log.tag?.let { palette[abs(it.hashCode()) % palette.size] } ?: colors.content03
                } else {
                    log.level.themeColor(colors)
                }
            } else {
                statusColorState.value
            }
        }
    }

    Column(
        modifier = modifier
            .offset { IntOffset(drag.offsetX.roundToInt(), drag.offsetY.roundToInt()) }
            .widthIn(max = maxWidth)
            .clip(Theme.rounding.r12)
            .background(Theme.colors.background1)
            .drawWithContent {
                drawContent()
                // Reading borderColorState.value here (draw phase) means color changes
                // trigger a repaint, not a full recomposition of OverlayCard.
                val color = borderColorState.value
                val inset = borderStrokePx / 2f
                drawRoundRect(
                    color = color,
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - borderStrokePx, size.height - borderStrokePx),
                    style = Stroke(width = borderStrokePx),
                    cornerRadius = CornerRadius(cornerRadiusPx - inset),
                )
            }
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
            OverlayEnabledSection(
                isEnabled = state.isEnabled,
                isExpanded = state.isExpanded,
                statusText = state.statusText,
                caughtCount = state.caughtCount,
                badgeColor = borderColorState,
            )
            OverlayIconControls(
                isEnabled = state.isEnabled,
                isPaused = state.isPaused,
                canStep = state.canStep,
                dispatch = dispatch,
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

@DsPreview
@Composable
private fun PreviewStepperOverlayContentDisabled() {
    ThemeProvider {
        StepperOverlayContent(
            state = StepperOverlayState(),
            dispatch = {},
        )
    }
}

@DsPreview
@Composable
private fun PreviewStepperOverlayContentEnabled() {
    val state = StepperOverlayState()
    state.isEnabled.preview(true)
    state.isPaused.preview(true)
    state.caughtCount.preview(3)
    state.isExpanded.preview(true)
    ThemeProvider {
        StepperOverlayContent(
            state = state,
            dispatch = {},
        )
    }
}
