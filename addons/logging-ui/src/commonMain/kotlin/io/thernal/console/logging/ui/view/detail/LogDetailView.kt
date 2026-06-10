package io.thernal.console.logging.ui.view.detail

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.api.ui.LocalLogRenderer
import io.thernal.console.logging.ui.view.detail.model.LogDetailViewModel

@Composable
internal fun LogDetailView(
    logId: String,
    groupId: String,
) {
    val viewModel = viewModel(key = "$groupId:$logId") { LogDetailViewModel(logId = logId, groupId = groupId) }
    val navigator = LocalConsoleNavigator.current
    val renderer = LocalLogRenderer.current

    LogDetailContent(
        state = viewModel.state,
        onDispatch = viewModel::dispatch,
        onBack = navigator::pop,
        renderer = renderer,
    )
}
