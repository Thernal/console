package az.theternal.console.compose

import androidx.compose.runtime.Composable
import az.theternal.console.api.LogRenderer
import az.theternal.console.runtime.Log
import az.theternal.console.compose.renderer.detail.DefaultLogDetail
import az.theternal.console.compose.renderer.item.DefaultLogItem

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
    ) = detail?.invoke(log, onBack) ?: DefaultLogDetail(log, onBack)
}
