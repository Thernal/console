package io.thernal.console.compose.trigger

import io.thernal.console.api.trigger.ConsoleTrigger

actual fun ConsoleTrigger.Companion.shake(threshold: Float): ConsoleTrigger = none()
