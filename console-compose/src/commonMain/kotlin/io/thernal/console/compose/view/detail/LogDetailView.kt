package io.thernal.console.compose.view.detail

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.api.ui.LocalLogRenderer
import io.thernal.console.compose.view.detail.model.LogDetailViewModel

@Composable
internal fun LogDetailView(logId: String) {
    val viewModel = viewModel(key = logId) { LogDetailViewModel(logId) }
    val navigator = LocalConsoleNavigator.current
    val renderer = LocalLogRenderer.current

    LogDetailContent(
        log = viewModel.state.log.value,
        onBack = navigator::pop,
        renderer = renderer,
    )
}
