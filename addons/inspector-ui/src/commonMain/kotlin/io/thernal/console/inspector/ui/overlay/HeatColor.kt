package io.thernal.console.inspector.ui.overlay

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

private val cold = Color(0xFF3B82F6)
private val warm = Color(0xFFFACC15)
private val hot = Color(0xFFEF4444)
private const val HALF = 0.5f

/** Blue → yellow → red as [count] approaches [threshold]; saturates at the threshold. */
internal fun heatColor(
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
