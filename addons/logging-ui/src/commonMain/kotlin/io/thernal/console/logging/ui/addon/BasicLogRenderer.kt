package io.thernal.console.logging.ui.addon

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.api.ui.LogRenderer
import io.thernal.console.logging.ui.components.BasicLogItem
import io.thernal.console.runtime.log.Log
import io.thernal.console.logging.ui.view.defaultdetail.BasicLogDetailView

class BasicLogRenderer(
    private val itemDefault: (@Composable (Log, Modifier) -> Unit)? = null,
    private val detailDefault: (@Composable (Log) -> Unit)? = null,
) : LogRenderer {
    @Composable
    override fun Item(
        log: Log,
        modifier: Modifier,
    ) {
        return itemDefault?.invoke(log, modifier) ?: BasicLogItem(log, modifier)
    }

    @Composable
    override fun Detail(log: Log) {
        return detailDefault?.invoke(log) ?: BasicLogDetailView(log)
    }
}
