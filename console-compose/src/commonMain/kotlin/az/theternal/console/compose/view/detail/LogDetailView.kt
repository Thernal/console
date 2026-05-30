package az.theternal.console.compose.view.detail

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import az.theternal.console.api.navigation.LocalConsoleNavigator
import az.theternal.console.api.ui.LocalLogRenderer
import az.theternal.console.compose.view.detail.model.LogDetailViewModel

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
