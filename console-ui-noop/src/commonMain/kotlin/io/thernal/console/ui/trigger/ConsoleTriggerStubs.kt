@file:Suppress("UnusedParameter")

package io.thernal.console.ui.trigger

import androidx.compose.ui.input.key.Key
import io.thernal.console.api.trigger.ConsoleTrigger
import io.thernal.console.api.trigger.Corner
import io.thernal.console.api.trigger.KeyModifier
import io.thernal.console.api.trigger.Swipe
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private const val DEFAULT_SHAKE_THRESHOLD = 12f
private const val DEFAULT_AREA_FRACTION = 0.15f
private val DEFAULT_LONG_PRESS_DURATION = 500.milliseconds

fun ConsoleTrigger.Companion.swipeSequence(
    vararg swipes: Swipe,
    threshold: Float = 50f,
): ConsoleTrigger = ConsoleTrigger { this }

fun ConsoleTrigger.Companion.doubleTap(): ConsoleTrigger {
    return ConsoleTrigger { this }
}

fun ConsoleTrigger.Companion.longPress(duration: Duration = DEFAULT_LONG_PRESS_DURATION): ConsoleTrigger =
    ConsoleTrigger { this }

fun ConsoleTrigger.Companion.multiFingerTap(count: Int = 3): ConsoleTrigger = ConsoleTrigger { this }

fun ConsoleTrigger.Companion.tapCorner(
    corner: Corner,
    count: Int = 5,
    areaFraction: Float = DEFAULT_AREA_FRACTION,
): ConsoleTrigger = ConsoleTrigger { this }

fun ConsoleTrigger.Companion.shake(threshold: Float = DEFAULT_SHAKE_THRESHOLD): ConsoleTrigger = ConsoleTrigger { this }

fun ConsoleTrigger.Companion.keyboardShortcut(
    key: Key,
    modifiers: Set<KeyModifier> = emptySet(),
): ConsoleTrigger = ConsoleTrigger { this }

fun ConsoleTrigger.Companion.none(): ConsoleTrigger {
    return ConsoleTrigger { this }
}

fun ConsoleTrigger.Companion.any(vararg triggers: ConsoleTrigger): ConsoleTrigger {
    return ConsoleTrigger { this }
}
