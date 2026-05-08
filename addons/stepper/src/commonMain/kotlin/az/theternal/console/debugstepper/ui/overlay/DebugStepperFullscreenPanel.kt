package az.theternal.console.debugstepper.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import az.theternal.console.ui.ds.DsDivider
import az.theternal.console.ui.ds.DsIcon
import az.theternal.console.ui.ds.DsText
import az.theternal.console.ui.ds.DsTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import az.theternal.console.debugstepper.DebugStepperState
import androidx.compose.foundation.clickable

@Composable
internal fun DebugStepperFullscreenPanel(
    visible: Boolean,
    uiState: DebugStepperOverlayUiState,
    stepperState: DebugStepperState,
    onDismiss: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(FULLSCREEN_SLIDE_IN_MS),
        ) + fadeIn(tween(FULLSCREEN_FADE_IN_MS)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(FULLSCREEN_SLIDE_OUT_MS),
        ) + fadeOut(tween(FULLSCREEN_FADE_OUT_MS)),
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OverlayBg)
                .windowInsetsPadding(WindowInsets.systemBars),
        ) {
            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DsIcon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onDismiss() },
                )

                // Status dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(uiState.statusColor),
                )

                DsText(
                    text = "Debug Stepper",
                    color = Color.White,
                    style = DsTextStyle.TitleMedium,
                    modifier = Modifier.weight(1f),
                )

                DsText(
                    text = uiState.statusText,
                    color = Color(0xFF9CA3AF),
                    fontSize = 11.sp,
                )

                DebugStepperIconControls(
                    enabled = stepperState.enabled,
                    paused = stepperState.paused,
                    canStep = uiState.canStep,
                )
            }

            DsDivider(color = Color(0xFF2D3142))

            // Scrollable content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Status info box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E2029))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    DsText(
                        text = "Status: ${uiState.statusText}",
                        color = uiState.statusColor,
                        fontSize = 13.sp,
                    )
                    DsText(
                        text = "Current step: ${stepperState.currentStep ?: "—"}",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp,
                    )
                    DsText(
                        text = "Queued logs: ${stepperState.pendingLogs}",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp,
                    )
                    DsText(
                        text = "Can step now: ${uiState.canStep}",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp,
                    )
                }

                // Log content
                Spacer(Modifier.height(0.dp))
                if (uiState.currentLog != null && stepperState.enabled) {
                    SelectionContainer {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E2029))
                                .padding(12.dp),
                        ) {
                            DsText(
                                text = uiState.currentLog.message,
                                color = Color.White,
                                fontSize = 12.sp,
                            )
                        }
                    }
                } else {
                    DsText(
                        text = "No log received yet.",
                        color = Color(0xFF6B7280),
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}
