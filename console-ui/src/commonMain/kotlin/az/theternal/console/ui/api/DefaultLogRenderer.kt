package az.theternal.console.ui.api

import androidx.compose.runtime.Composable
import az.theternal.console.addon.api.renderer.LogRenderer
import az.theternal.console.Log
import az.theternal.console.ui.renderer.defaultlogdetail.DefaultLogDetail
import az.theternal.console.ui.renderer.defaultlogitem.DefaultLogItem

val DefaultLogRenderer: LogRenderer = defaultLogRenderer()

/**
 * Returns a [LogRenderer] that falls back to the built-in item/detail implementations.
 * Pass non-null lambdas to override only the parts you need.
 *
 * Example — custom detail, default item:
 * ```
 * ConsoleInstaller(logRenderer = defaultLogRenderer(detail = { log, onBack -> MyDetail(log, onBack) }))
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
