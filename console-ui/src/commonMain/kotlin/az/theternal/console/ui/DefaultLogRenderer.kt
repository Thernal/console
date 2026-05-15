package az.theternal.console.ui

import androidx.compose.runtime.Composable
import az.theternal.console.runtime.model.Log
import az.theternal.console.ui.renderer.defaultlogdetail.DefaultLogDetail
import az.theternal.console.ui.renderer.defaultlogitem.DefaultLogItem
import az.theternal.console.ui.renderer.LogRenderer

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
