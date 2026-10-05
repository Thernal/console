package io.thernal.console.inspector.ui.overlay

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/** The three stops of the heatmap scale, taken from the theme. */
internal class HeatColors(
    val cold: Color,
    val warm: Color,
    val hot: Color,
)

/** Cold → warm → hot as [count] approaches [threshold]; saturates at the threshold. */
internal fun HeatColors.colorFor(
    count: Int,
    threshold: Int,
): Color {
    val fraction = (count.toFloat() / threshold.coerceAtLeast(1)).coerceIn(0f, 1f)
    return if (fraction < HALF) {
        lerp(cold, warm, fraction / HALF)
    } else {
        lerp(warm, hot, (fraction - HALF) / HALF)
    }
}

private const val HALF = 0.5f
