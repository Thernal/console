package io.thernal.console.ui.trigger

import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import io.thernal.console.api.trigger.ConsoleTrigger

fun ConsoleTrigger.Companion.multiFingerTap(count: Int = 3): ConsoleTrigger {
    return ConsoleTrigger { onDetected ->
        pointerInput(count, onDetected) {
            awaitMultiFingerTap(
                requiredCount = count,
                onDetected = onDetected,
            )
        }
    }
}

private suspend fun PointerInputScope.awaitMultiFingerTap(
    requiredCount: Int,
    onDetected: () -> Unit,
) {
    val state = MultiFingerTapState(requiredCount)
    awaitPointerEventScope {
        while (true) {
            val pressedCount = awaitPressedCount()
            if (state.update(pressedCount)) {
                onDetected()
            }
        }
    }
}

private suspend fun AwaitPointerEventScope.awaitPressedCount(): Int {
    return awaitPointerEvent(PointerEventPass.Initial)
        .changes
        .count { it.pressed }
}

private class MultiFingerTapState(
    private val requiredCount: Int,
) {
    private var maxPressedCount = 0
    private var fired = false
    fun update(pressedCount: Int): Boolean {
        maxPressedCount = maxOf(maxPressedCount, pressedCount)
        val shouldFire = !fired && maxPressedCount >= requiredCount
        if (shouldFire) {
            fired = true
        }
        if (pressedCount == 0) {
            reset()
        }
        return shouldFire
    }

    private fun reset() {
        maxPressedCount = 0
        fired = false
    }
}
