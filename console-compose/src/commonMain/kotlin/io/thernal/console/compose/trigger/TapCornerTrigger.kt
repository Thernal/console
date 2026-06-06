package io.thernal.console.compose.trigger

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import io.thernal.console.api.trigger.ConsoleTrigger
import io.thernal.console.api.trigger.Corner
private const val DEFAULT_AREA_FRACTION = 0.15f

fun ConsoleTrigger.Companion.tapCorner(
    corner: Corner,
    count: Int = 5,
    areaFraction: Float = DEFAULT_AREA_FRACTION,
): ConsoleTrigger {
    return ConsoleTrigger { onDetected ->
        pointerInput(corner, count, areaFraction, onDetected) {
            awaitTapCornerTrigger(
                corner = corner,
                requiredCount = count,
                areaFraction = areaFraction,
                onDetected = onDetected,
            )
        }
    }
}

private suspend fun PointerInputScope.awaitTapCornerTrigger(
    corner: Corner,
    requiredCount: Int,
    areaFraction: Float,
    onDetected: () -> Unit,
) {
    var tapCount = 0
    awaitPointerEventScope {
        while (true) {
            val position = awaitDownPosition() ?: continue
            tapCount = if (position.isInCorner(size, corner, areaFraction)) {
                (tapCount + 1).also { newCount ->
                    if (newCount >= requiredCount) {
                        onDetected()
                    }
                } % requiredCount
            } else {
                0
            }
        }
    }
}
private suspend fun AwaitPointerEventScope.awaitDownPosition(): Offset? {
    return awaitPointerEvent(PointerEventPass.Initial)
        .changes
        .firstOrNull { it.changedToDown() }
        ?.position
}
private fun Offset.isInCorner(
    size: IntSize,
    corner: Corner,
    areaFraction: Float,
): Boolean {
    val width = size.width.toFloat()
    val height = size.height.toFloat()
    val left = x < width * areaFraction
    val right = x > width * (1f - areaFraction)
    val top = y < height * areaFraction
    val bottom = y > height * (1f - areaFraction)
    return when (corner) {
        Corner.TopLeft -> left && top
        Corner.TopRight -> right && top
        Corner.BottomLeft -> left && bottom
        Corner.BottomRight -> right && bottom
    }
}
