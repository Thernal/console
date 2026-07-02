package io.thernal.console.logging.ui.view.defaultdetail

import androidx.compose.runtime.Composable
import io.thernal.console.core.log.Log

@Composable
internal fun BasicLogDetailView(log: Log) {
    DefaultLogDetailContent(log = log)
}
