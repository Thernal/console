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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SkipNext
import az.theternal.console.ui.ds.DsDivider
import az.theternal.console.ui.ds.DsIcon
import az.theternal.console.ui.ds.DsText
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import az.theternal.console.debugstepper.DebugStepper
import az.theternal.console.debugstepper.DebugStepperState
import az.theternal.console.debugstepper.ui.DebugStepperNavGraph
import az.theternal.console.ui.ConsoleRoute
import az.theternal.console.ui.LocalConsoleNavigator
import az.theternal.console.ui.LocalLogRenderer
import kotlin.math.roundToInt

private val IconSize = 20.dp
private val SmallIconSize = 18.dp

@Composable
internal fun DebugStepperFloatingCard(
    uiState: DebugStepperOverlayUiState,
    stepperState: DebugStepperState,
) {
    val navigator = LocalConsoleNavigator.current
    val renderer = LocalLogRenderer.current
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var offsetX by rememberSaveable { mutableFloatStateOf(0f) }
    var offsetY by rememberSaveable { mutableFloatStateOf(0f) }

    val enabled = stepperState.enabled

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
                    .clip(RoundedCornerShape(12.dp))
                    .background(OverlayBg)
                    .border(
                        width = 1.dp,
                        color = uiState.statusColor.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .then(if (enabled && isExpanded) Modifier.width(220.dp) else Modifier),
            ) {
                when {
                    // ── Disabled: power + expand only ─────────────────────────
                    !enabled -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            DsIcon(
                                imageVector = Icons.Outlined.PowerSettingsNew,
                                contentDescription = "Enable stepper",
                                modifier = Modifier.size(IconSize).clickable { DebugStepper.setEnabled(true) },
                                tint = DisabledColor,
                            )
                            ExpandIcon(isExpanded = isExpanded, onClick = { isExpanded = !isExpanded })
                        }
                    }

                    // ── Enabled + collapsed: icons only ───────────────────────
                    !isExpanded -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(uiState.statusColor),
                            )
                            DsIcon(
                                imageVector = Icons.Outlined.PowerSettingsNew,
                                contentDescription = "Disable stepper",
                                modifier = Modifier.size(IconSize).clickable { DebugStepper.setEnabled(false) },
                                tint = Color.White,
                            )
                            DsIcon(
                                imageVector = if (stepperState.paused) {
                                    Icons.Outlined.PlayArrow
                                } else {
                                    Icons.Outlined.Pause
                                },
                                contentDescription = if (stepperState.paused) "Resume" else "Pause",
                                modifier = Modifier
                                    .size(IconSize)
                                    .clickable { DebugStepper.setPaused(!stepperState.paused) },
                                tint = Color.White,
                            )
                            DsIcon(
                                imageVector = Icons.Outlined.SkipNext,
                                contentDescription = "Step",
                                modifier = Modifier.size(IconSize).clickable { DebugStepper.next() },
                                tint = if (uiState.canStep) Color.White else DisabledColor,
                            )
                            DsIcon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Stepper settings",
                                tint = ContentMutedColor,
                                modifier = Modifier
                                    .size(SmallIconSize)
                                    .clickable { navigator?.openTab(DebugStepperNavGraph.title) },
                            )
                            ExpandIcon(isExpanded = false, onClick = { isExpanded = true })
                        }
                    }

                    // ── Enabled + expanded: full header + log content ─────────
                    else -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(uiState.statusColor),
                            )
                            DsText(
                                text = uiState.displayTag ?: "Stepper",
                                color = Color.White,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                            DsText(
                                text = uiState.statusText,
                                color = ContentMutedColor,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            DsIcon(
                                imageVector = Icons.Outlined.PowerSettingsNew,
                                contentDescription = "Disable stepper",
                                modifier = Modifier.size(IconSize).clickable { DebugStepper.setEnabled(false) },
                                tint = Color.White,
                            )
                            DsIcon(
                                imageVector = if (stepperState.paused) {
                                    Icons.Outlined.PlayArrow
                                } else {
                                    Icons.Outlined.Pause
                                },
                                contentDescription = if (stepperState.paused) "Resume" else "Pause",
                                modifier = Modifier
                                    .size(IconSize)
                                    .clickable { DebugStepper.setPaused(!stepperState.paused) },
                                tint = Color.White,
                            )
                            DsIcon(
                                imageVector = Icons.Outlined.SkipNext,
                                contentDescription = "Step",
                                modifier = Modifier.size(IconSize).clickable { DebugStepper.next() },
                                tint = if (uiState.canStep) Color.White else DisabledColor,
                            )
                            DsIcon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Stepper settings",
                                tint = ContentMutedColor,
                                modifier = Modifier
                                    .size(SmallIconSize)
                                    .clickable { navigator?.openTab(DebugStepperNavGraph.title) },
                            )
                            ExpandIcon(isExpanded = true, onClick = { isExpanded = false })
                        }

                        DsDivider(
                            modifier = Modifier.padding(vertical = 6.dp),
                            color = OverlayDividerColor,
                        )
                        if (uiState.currentLog != null) {
                            renderer.Item(
                                log = uiState.currentLog,
                                onClick = {
                                    navigator?.push(ConsoleRoute.LogDetail("", uiState.currentLog.id))
                                },
                            )
                        } else {
                            DsText(
                                text = "Nothing caught yet.",
                                color = ContentMutedColor,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(vertical = 4.dp),
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
    onClick: () -> Unit,
) {
    DsIcon(
        imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
        contentDescription = if (isExpanded) "Collapse" else "Expand",
        tint = ContentMutedColor,
        modifier = Modifier.size(SmallIconSize).clickable(onClick = onClick),
    )
}
