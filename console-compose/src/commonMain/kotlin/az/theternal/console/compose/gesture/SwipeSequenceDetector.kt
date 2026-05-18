package az.theternal.console.compose.gesture

import androidx.compose.ui.Modifier
import az.theternal.console.compose.gesture.Swipe
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

internal fun Modifier.swipeSequenceDetector(
    sequence: List<Swipe>,
    threshold: Float = 50f,
    onDetected: () -> Unit,
): Modifier = pointerInput(sequence, threshold, onDetected) {
    val input = mutableListOf<Swipe>()

    awaitPointerEventScope {
        while (true) {
            val down = awaitPointerEvent(PointerEventPass.Initial)
            val downChange = down.changes.firstOrNull { it.pressed } ?: continue
            val start = downChange.position

            val end = awaitGestureEndPosition(start)

            val direction = (start to end).toSwipeDirection(threshold) ?: continue
            if (input.pushAndMatches(direction, sequence)) onDetected()
        }
    }
}

private suspend fun AwaitPointerEventScope.awaitGestureEndPosition(start: Offset): Offset {
    var end = start
    while (true) {
        val move = awaitPointerEvent(PointerEventPass.Initial)
        val change = move.changes.firstOrNull() ?: break
        end = change.position
        if (!change.pressed) break
    }
    return end
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
    if (size > sequence.size) removeAt(0)
    if (this != sequence) return false
    clear()
    return true
}
