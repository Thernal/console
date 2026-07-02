package io.thernal.console.crash.ui.view.logdetail

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.api.ui.LocalLogRenderer
import io.thernal.console.crash.ui.view.logdetail.model.CrashLogDetailViewModel
import io.thernal.console.ui.logdetail.LogDetailContent

@Composable
internal fun CrashLogDetailView(
    sessionId: String,
    logId: String,
) {
    val viewModel = viewModel(key = "$sessionId:$logId") {
        CrashLogDetailViewModel(sessionId = sessionId, logId = logId)
    }
    val navigator = LocalConsoleNavigator.current
    val renderer = LocalLogRenderer.current

    LogDetailContent(
        state = viewModel.state,
        onDispatch = viewModel::dispatch,
        onBack = navigator::pop,
        renderer = renderer,
    )
}
