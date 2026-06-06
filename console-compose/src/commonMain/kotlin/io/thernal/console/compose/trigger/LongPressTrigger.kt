package io.thernal.console.compose.trigger

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.input.pointer.pointerInput
import io.thernal.console.api.trigger.ConsoleTrigger
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val DEFAULT_LONG_PRESS_DURATION = 500.milliseconds

fun ConsoleTrigger.Companion.longPress(duration: Duration = DEFAULT_LONG_PRESS_DURATION): ConsoleTrigger =
    ConsoleTrigger { onDetected ->
        pointerInput(duration, onDetected) {
            while (true) {
                // Step 1 — wait for a touch down (restricted scope ends here)
                awaitPointerEventScope {
                    awaitFirstDown(requireUnconsumed = false)
                }
                // Step 2 — back in PointerInputScope: withTimeoutOrNull is allowed
                // null means the finger was held longer than `duration` — that is the long press
                val up = withTimeoutOrNull(duration) {
                    awaitPointerEventScope { waitForUpOrCancellation() }
                }
                if (up == null) onDetected()
            }
        }
    }
