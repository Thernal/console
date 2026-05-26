package az.theternal.console.api.trigger

import androidx.compose.ui.Modifier

fun interface ConsoleTrigger {
    fun Modifier.attach(onDetected: () -> Unit): Modifier
    companion object
}
