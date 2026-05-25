package az.theternal.console.compose.trigger

import androidx.compose.ui.Modifier

/**
 * Defines how the console is triggered via a [Modifier]-based gesture.
 *
 * Built-in factory: [ConsoleTrigger.swipeSequence]
 *
 * Custom example — double-tap anywhere:
 *   val myTrigger = ConsoleTrigger { onDetected ->
 *       pointerInput(Unit) {
 *           detectTapGestures(onDoubleTap = { onDetected() })
 *       }
 *   }
 */
fun interface ConsoleTrigger {
    fun Modifier.attach(onDetected: () -> Unit): Modifier

    companion object {
        // threshold is in pixels
        fun swipeSequence(
            vararg swipes: Swipe,
            threshold: Float = 50f,
        ): ConsoleTrigger = ConsoleTrigger { onDetected ->
            swipeSequenceDetector(
                sequence = swipes.toList(),
                threshold = threshold,
                onDetected = onDetected,
            )
        }
    }
}
