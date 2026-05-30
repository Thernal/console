package az.theternal.console.compose.view.logs

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import az.theternal.console.api.navigation.ConsoleRoute
import az.theternal.console.compose.view.logs.model.LogsViewModel
import az.theternal.console.api.navigation.LocalConsoleNavigator
import az.theternal.console.api.ui.LocalLogRenderer

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
