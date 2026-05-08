package az.theternal.console.debugstepper.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import az.theternal.console.ui.ds.DsText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun DebugStepperScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        DsText("Debug Stepper")
    }
}
