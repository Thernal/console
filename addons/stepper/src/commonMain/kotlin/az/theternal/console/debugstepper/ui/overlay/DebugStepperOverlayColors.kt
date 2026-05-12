package az.theternal.console.debugstepper.ui.overlay

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
internal fun overlayStatusColor(tone: StepperStatusTone): Color {
    return when (tone) {
        StepperStatusTone.Disabled -> Theme.colors.content03
        StepperStatusTone.Paused -> Theme.colors.warning
        StepperStatusTone.Idle -> Theme.colors.info
        StepperStatusTone.Running -> Theme.colors.success
    }
}

val overlayBackgroundColor: Color
    @Composable get() = Theme.colors.background1

val overlayMutedContentColor: Color
    @Composable get() = Theme.colors.content04

val overlayDividerColor: Color
    @Composable get() = Theme.colors.border
