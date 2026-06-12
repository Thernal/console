@file:Suppress("UnusedParameter")

package io.thernal.console.ui

import androidx.compose.runtime.Composable
import io.thernal.console.api.trigger.ConsoleTrigger

@Composable
fun ConsoleProvider(
    enabled: Boolean = false,
    trigger: ConsoleTrigger = ConsoleTrigger { this },
    content: @Composable () -> Unit,
) {
    content()
}
