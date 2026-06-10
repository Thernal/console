package io.thernal.console.network.ui.components.networklogitem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.network.NetworkLog

@Composable
internal fun NetworkLogMeta(log: NetworkLog) {
    DsText(
        text = log.metaInfo(),
        style = Theme.typography.body03,
        color = Theme.colors.content03,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

private fun NetworkLog.metaInfo(): String {
    val parts = buildList {
        add("${headers.size} headers")
        body?.takeIf { it.isNotBlank() }?.let { add("${it.length} chars") }
        if (this@metaInfo is NetworkLog.Response) {
            durationMs?.let { add("${it}ms") }
        }
    }
    return parts.joinToString(separator = " • ")
}
