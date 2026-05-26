package az.theternal.console.compose.trigger

import az.theternal.console.api.trigger.ConsoleTrigger

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
