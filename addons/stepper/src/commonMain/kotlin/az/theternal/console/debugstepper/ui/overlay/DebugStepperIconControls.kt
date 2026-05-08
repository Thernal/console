package az.theternal.console.debugstepper.ui.overlay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.SkipNext
import az.theternal.console.ui.ds.DsIcon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import az.theternal.console.debugstepper.DebugStepper

@Composable
internal fun DebugStepperIconControls(
    enabled: Boolean,
    paused: Boolean,
    canStep: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsIcon(
            imageVector = Icons.Outlined.PowerSettingsNew,
            contentDescription = "Toggle stepper",
            modifier = Modifier.size(20.dp).clickable { DebugStepper.setEnabled(!enabled) },
            tint = if (enabled) Color.White else DisabledColor,
        )
        if (enabled) {
            DsIcon(
                imageVector = if (paused) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
                contentDescription = if (paused) "Resume" else "Pause",
                modifier = Modifier.size(20.dp).clickable { DebugStepper.setPaused(!paused) },
                tint = Color.White,
            )
            DsIcon(
                imageVector = Icons.Outlined.SkipNext,
                contentDescription = "Step",
                modifier = Modifier.size(20.dp).clickable { DebugStepper.next() },
                tint = if (canStep) Color.White else DisabledColor,
            )
        }
    }
}
