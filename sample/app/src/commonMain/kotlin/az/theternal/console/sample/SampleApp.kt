package az.theternal.console.sample

import androidx.compose.runtime.Composable
import az.theternal.console.sample.counter.CounterScreen
import az.theternal.console.ui.api.ConsoleInstaller

@Composable
fun SampleApp() {
    ConsoleInstaller {
        CounterScreen()
    }
}
