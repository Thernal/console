package az.theternal.console.sample

import androidx.compose.runtime.Composable
import az.theternal.console.info.DetailsPlugin
import az.theternal.console.debugstepper.StepperPlugin
import az.theternal.console.sample.counter.CounterScreen
import az.theternal.console.ui.ConsoleInstaller

@Composable
fun SampleApp() {
    ConsoleInstaller(
        plugins = listOf(DetailsPlugin, StepperPlugin),
    ) {
        CounterScreen()
    }
}
