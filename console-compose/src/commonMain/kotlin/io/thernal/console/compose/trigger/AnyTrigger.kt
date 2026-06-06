package io.thernal.console.compose.trigger

import io.thernal.console.api.trigger.ConsoleTrigger

fun ConsoleTrigger.Companion.any(vararg triggers: ConsoleTrigger): ConsoleTrigger {
    return ConsoleTrigger { onDetected ->
        triggers.fold(this) { modifier, trigger ->
            with(trigger) { modifier.attach(onDetected) }
        }
    }
}
