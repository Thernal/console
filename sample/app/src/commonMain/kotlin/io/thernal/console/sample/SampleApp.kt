package io.thernal.console.sample

import androidx.compose.runtime.Composable
import io.thernal.console.compose.ConsoleProvider
import io.thernal.console.sample.counter.CounterScreen

@Composable
fun SampleApp() {
    ConsoleProvider {
        CounterScreen()
    }
}
