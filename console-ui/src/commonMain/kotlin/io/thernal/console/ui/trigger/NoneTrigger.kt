package io.thernal.console.ui.trigger

import io.thernal.console.api.trigger.ConsoleTrigger

fun ConsoleTrigger.Companion.none(): ConsoleTrigger {
    return ConsoleTrigger { _ -> this }
}
