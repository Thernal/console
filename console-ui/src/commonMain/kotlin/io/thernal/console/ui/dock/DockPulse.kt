package io.thernal.console.ui.dock

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

private const val PULSE_MIN = 0.35f
private const val PULSE_MILLIS = 900

/** A slow 1 → 0.35 → 1 fade while [isPulsing], a steady 1 otherwise, so calm widgets cost no animation. */
@Composable
internal fun rememberPulse(isPulsing: Boolean): Float {
    if (!isPulsing) return 1f
    val transition = rememberInfiniteTransition(label = "dock-pulse")
    val alpha by transition.animateFloat(
        initialValue = PULSE_MIN,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(PULSE_MILLIS, easing = LinearEasing), RepeatMode.Reverse),
        label = "dock-pulse-alpha",
    )
    return alpha
}
