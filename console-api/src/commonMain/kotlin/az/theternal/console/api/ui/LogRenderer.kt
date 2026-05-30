package az.theternal.console.api.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import az.theternal.console.runtime.Log

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

val LocalLogRenderer = compositionLocalOf<LogRenderer> { NoOpLogRenderer }

object NoOpLogRenderer : LogRenderer {
    @Composable override fun Item(
        log: Log,
        onClick: () -> Unit,
    ) = Unit

    @Composable override fun Detail(
        log: Log,
        onBack: () -> Unit,
    ) = Unit
}
