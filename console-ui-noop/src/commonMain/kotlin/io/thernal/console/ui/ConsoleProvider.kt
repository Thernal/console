@file:Suppress("UnusedParameter")

package io.thernal.console.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import io.thernal.console.api.trigger.ConsoleTrigger
import io.thernal.console.runtime.console.Console

@Composable
fun ConsoleProvider(
    enabled: Boolean = false,
    trigger: ConsoleTrigger = ConsoleTrigger { this },
    content: @Composable () -> Unit,
) {
    SideEffect {
        Console.isEnabled = false
    }

    content()
}
