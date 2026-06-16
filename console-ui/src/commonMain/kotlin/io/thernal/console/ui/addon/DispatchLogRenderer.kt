package io.thernal.console.ui.addon

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.api.ui.LogRenderer
import io.thernal.console.api.ui.LogRendererRegistry
import io.thernal.console.core.log.Log

internal class DispatchLogRenderer : LogRenderer {

    @Composable
    override fun Item(
        log: Log,
        modifier: Modifier,
    ) {
        LogRendererRegistry.find(log)?.Item(
            log = log,
            modifier = modifier,
        )
    }

    @Composable
    override fun Detail(log: Log) {
        LogRendererRegistry.find(log)?.Detail(log)
    }
}
