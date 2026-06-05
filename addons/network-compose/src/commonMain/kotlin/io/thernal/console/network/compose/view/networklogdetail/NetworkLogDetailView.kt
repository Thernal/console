package io.thernal.console.network.compose.view.networklogdetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalClipboard
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.compose.common.toTextClipEntry
import io.thernal.console.network.NetworkLog
import io.thernal.console.network.compose.view.networklogdetail.model.NetworkLogDetailEffect
import io.thernal.console.network.compose.view.networklogdetail.model.NetworkLogDetailViewModel

@Composable
internal fun NetworkLogDetailView(log: NetworkLog) {
    val viewModel = viewModel(key = log.id) { NetworkLogDetailViewModel(log = log) }
    val clipboard = LocalClipboard.current

    viewModel.OnEffectUpdate { effect ->
        when (effect) {
            is NetworkLogDetailEffect.Copy -> {
                clipboard.setClipEntry(effect.data.toTextClipEntry())
            }
        }
    }

    NetworkLogDetailContent(
        log = log,
        state = viewModel.state,
        onDispatch = viewModel::dispatch,
    )
}
