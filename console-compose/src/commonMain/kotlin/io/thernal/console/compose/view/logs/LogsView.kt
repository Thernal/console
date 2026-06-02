package io.thernal.console.compose.view.logs

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.api.navigation.ConsoleRoute
import io.thernal.console.compose.view.logs.model.LogsViewModel
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.api.ui.LocalLogRenderer

@Composable
internal fun LogsView() {
    val viewModel = viewModel { LogsViewModel() }
    val navigator = LocalConsoleNavigator.current
    val renderer = LocalLogRenderer.current

    LogsContent(
        state = viewModel.state,
        renderer = renderer,
        dispatch = viewModel::dispatch,
        onLogClick = { log ->
            navigator.push(
                key = ConsoleRoute.LogDetail(
                    groupId = "",
                    logId = log.id,
                ),
            )
        },
    )
}
