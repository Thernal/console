package io.thernal.console.compose.trigger

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import io.thernal.console.api.trigger.ConsoleTrigger

fun ConsoleTrigger.Companion.doubleTap(): ConsoleTrigger {
    return ConsoleTrigger { onDetected ->
        pointerInput(onDetected) {
            detectTapGestures(onDoubleTap = { onDetected() })
        }
    }
}
