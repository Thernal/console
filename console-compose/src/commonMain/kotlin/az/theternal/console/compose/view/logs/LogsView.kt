package az.theternal.console.compose.view.logs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import az.theternal.console.api.navigation.ConsoleRoute
import az.theternal.console.api.navigation.LocalConsoleNavigator
import az.theternal.console.compose.ConsoleLogObserver
import az.theternal.console.api.ui.LocalLogRenderer

@Composable
internal fun LogsView() {
    val navigator = LocalConsoleNavigator.current
    val logs by ConsoleLogObserver.logs.collectAsState()
    val renderer = LocalLogRenderer.current

    LogsContent(
        logs = logs,
        onLogClick = { log ->
            navigator.push(
                key = ConsoleRoute.LogDetail(
                    groupId = "",
                    logId = log.id,
                ),
            )
        },
        renderer = renderer,
    )
}
