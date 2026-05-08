package az.theternal.console.ui.gesture

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.awaitPointerEventScope
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.geometry.Offset
import kotlin.math.abs

internal fun Modifier.swipeSequenceDetector(
    sequence: List<Swipe>,
    threshold: Float = 50f,
    onDetected: () -> Unit,
): Modifier = pointerInput(sequence, threshold, onDetected) {
    val input = mutableListOf<Swipe>()

    awaitPointerEventScope {
        while (true) {
            val drag = awaitSwipeDragOrNull() ?: continue
            val direction = drag.toSwipeDirection(threshold) ?: continue
            if (input.pushAndMatches(direction, sequence)) {
                onDetected()
            }
        }
    }
}

private suspend fun PointerInputScope.awaitSwipeDragOrNull(): Pair<Offset, Offset>? {
    val down = awaitPointerEvent().changes.firstOrNull { it.pressed } ?: return null
    val start = down.position
    var end = start

    while (true) {
        val change = awaitPointerEvent().changes.firstOrNull() ?: continue
        if (!change.pressed) {
            end = change.position - change.positionChange()
            break
        }
    }
    return start to end
}

private fun Pair<Offset, Offset>.toSwipeDirection(threshold: Float): Swipe? {
    val dx = second.x - first.x
    val dy = second.y - first.y
    return if (abs(dx) > abs(dy)) {
        when {
            dx > threshold -> Swipe.RIGHT
            dx < -threshold -> Swipe.LEFT
            else -> null
        }
    } else {
        when {
            dy > threshold -> Swipe.DOWN
            dy < -threshold -> Swipe.UP
            else -> null
        }
    }
}

private fun MutableList<Swipe>.pushAndMatches(
    direction: Swipe,
    sequence: List<Swipe>,
): Boolean {
    add(direction)
    if (size > sequence.size) {
        removeAt(0)
    }
    if (this != sequence) {
        return false
    }
    clear()
    return true
}
