package io.thernal.console.compose.components

import androidx.compose.runtime.Composable
import io.thernal.console.api.ui.LogRenderer
import io.thernal.console.runtime.Log
import io.thernal.console.compose.view.defaultdetail.DefaultLogDetailView

val DefaultLogRenderer: LogRenderer = defaultLogRenderer()

/**
 * Returns a [LogRenderer] that falls back to the built-in item/detail implementations.
 * Pass non-null lambdas to override only the parts you need.
 *
 * Example — custom detail, default item:
 * ```
 * ConsoleProvider(logRenderer = defaultLogRenderer(detail = { log, onBack -> MyDetail(log, onBack) }))
 * ```
 */
fun defaultLogRenderer(
    item: (@Composable (Log, () -> Unit) -> Unit)? = null,
    detail: (@Composable (Log, () -> Unit) -> Unit)? = null,
): LogRenderer = object : LogRenderer {
    @Composable
    override fun Item(
        log: Log,
        onClick: () -> Unit,
    ) = item?.invoke(log, onClick) ?: DefaultLogItem(log, onClick)

    @Composable
    override fun Detail(
        log: Log,
        onBack: () -> Unit,
    ) = detail?.invoke(log, onBack) ?: DefaultLogDetailView(log, onBack)
}
