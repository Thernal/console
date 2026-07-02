package io.thernal.console.crash.ui.view.sessions

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.crash.ui.navigation.CrashSessionRoute
import io.thernal.console.crash.ui.view.sessions.model.CrashSessionsViewModel

@Composable
internal fun CrashSessionsView() {
    val viewModel = viewModel { CrashSessionsViewModel() }
    val navigator = LocalConsoleNavigator.current

    CrashSessionsContent(
        state = viewModel.state,
        dispatch = viewModel::dispatch,
        onSessionClick = { session ->
            navigator.push(key = CrashSessionRoute(sessionId = session.id))
        },
    )
}
