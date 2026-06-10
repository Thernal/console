package io.thernal.console.ui.trigger

import io.thernal.console.api.trigger.ConsoleTrigger
import io.thernal.console.api.trigger.Swipe

/**
 * Built-in swipe-sequence trigger for the Console overlay.
 *
 * Custom example — double-tap anywhere:
 *   val myTrigger = ConsoleTrigger { onDetected ->
 *       pointerInput(Unit) {
 *           detectTapGestures(onDoubleTap = { onDetected() })
 *       }
 *   }
 */
fun ConsoleTrigger.Companion.swipeSequence(
    vararg swipes: Swipe,
    threshold: Float = 50f,
): ConsoleTrigger = ConsoleTrigger { onDetected ->
    swipeSequenceDetector(
        sequence = swipes.toList(),
        threshold = threshold,
        onDetected = onDetected,
    )
}
