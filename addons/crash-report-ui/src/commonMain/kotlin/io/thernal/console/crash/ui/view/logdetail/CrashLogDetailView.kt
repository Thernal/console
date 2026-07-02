package io.thernal.console.crash.ui.view.logdetail

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.crash.ui.view.logdetail.model.CrashLogDetailViewModel

@Composable
internal fun CrashLogDetailView(
    sessionId: String,
    logId: String,
) {
    val viewModel = viewModel(key = "$sessionId:$logId") {
        CrashLogDetailViewModel(sessionId = sessionId, logId = logId)
    }
    val navigator = LocalConsoleNavigator.current

    CrashLogDetailContent(
        state = viewModel.state,
        onBack = navigator::pop,
    )
}
