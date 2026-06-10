package io.thernal.console.sample

import androidx.compose.runtime.Composable
import io.thernal.console.ui.ConsoleProvider
import io.thernal.console.sample.counter.SampleScreen

@Composable
fun SampleApp() {
    ConsoleProvider {
        SampleScreen()
    }
}
