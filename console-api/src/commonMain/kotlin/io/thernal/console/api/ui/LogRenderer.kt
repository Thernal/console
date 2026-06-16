package io.thernal.console.api.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import io.thernal.console.core.log.Log

interface LogRenderer {
    @Composable
    fun Item(
        log: Log,
        modifier: Modifier,
    )

    @Composable
    fun Detail(log: Log)
}

val LocalLogRenderer = compositionLocalOf<LogRenderer> { NoOpLogRenderer }

object NoOpLogRenderer : LogRenderer {
    @Composable override fun Item(
        log: Log,
        modifier: Modifier,
    ) = Unit

    @Composable override fun Detail(log: Log) = Unit
}
