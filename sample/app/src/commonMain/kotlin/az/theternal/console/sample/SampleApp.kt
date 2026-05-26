package az.theternal.console.sample

import androidx.compose.runtime.Composable
import az.theternal.console.compose.ConsoleProvider
import az.theternal.console.sample.counter.CounterScreen

@Composable
fun SampleApp() {
    ConsoleProvider {
        CounterScreen()
    }
}
