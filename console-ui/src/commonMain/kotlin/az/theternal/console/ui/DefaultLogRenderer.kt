package az.theternal.console.ui

import androidx.compose.runtime.Composable
import az.theternal.console.core.base.Log
import az.theternal.console.ui.renderer.DefaultLogDetail
import az.theternal.console.ui.renderer.DefaultLogItem

internal object DefaultLogRenderer : LogRenderer {
    @Composable
    override fun Item(
        log: Log,
        onClick: () -> Unit,
    ) = DefaultLogItem(log, onClick)

    @Composable
    override fun Detail(
        log: Log,
        onBack: () -> Unit,
    ) = DefaultLogDetail(log, onBack)
}
