package az.theternal.console.stepper.compose.view.overlay

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import az.theternal.console.designsystem.foundation.theme.Theme

internal val overlayBackgroundColor = Color(0xFF13161C)
internal val overlayMutedContentColor = Color(0xFF6B7280)
internal val overlayDividerColor = Color(0xFF2A2D36)

@Composable
internal fun overlayStatusColor(tone: StepperStatusTone): Color {
    return when (tone) {
        StepperStatusTone.Disabled -> Theme.colors.content03
        StepperStatusTone.Paused -> Theme.colors.warning
        StepperStatusTone.Idle -> Theme.colors.info
        StepperStatusTone.Running -> Theme.colors.success
    }
}
