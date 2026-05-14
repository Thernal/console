package az.theternal.console.ui.renderer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import az.theternal.console.runtime.model.Log

interface LogRenderer {
    @Composable
    fun Item(
        log: Log,
        onClick: () -> Unit,
    )

    @Composable
    fun Detail(
        log: Log,
        onBack: () -> Unit,
    )
}

val LocalLogRenderer = compositionLocalOf<LogRenderer> {
    error("No LogRenderer provided — make sure ConsoleInstaller() is in the composition tree")
}
