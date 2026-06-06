package io.thernal.console.compose.trigger

import io.thernal.console.api.trigger.ConsoleTrigger

internal const val DEFAULT_SHAKE_THRESHOLD = 12f

expect fun ConsoleTrigger.Companion.shake(threshold: Float = DEFAULT_SHAKE_THRESHOLD): ConsoleTrigger
