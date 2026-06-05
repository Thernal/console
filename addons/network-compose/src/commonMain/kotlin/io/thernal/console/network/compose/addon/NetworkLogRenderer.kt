package io.thernal.console.network.compose.addon

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.api.ui.LogRenderer
import io.thernal.console.network.NetworkLog
import io.thernal.console.network.compose.components.networklogitem.NetworkLogItem
import io.thernal.console.network.compose.view.networklogdetail.NetworkLogDetailView
import io.thernal.console.runtime.log.Log

object NetworkLogRenderer : LogRenderer {
    @Composable
    override fun Item(
        log: Log,
        modifier: Modifier,
    ) {
        if (log is NetworkLog) {
            NetworkLogItem(log = log, modifier = modifier)
        }
    }

    @Composable
    override fun Detail(log: Log) {
        if (log is NetworkLog) {
            NetworkLogDetailView(log = log)
        }
    }
}
