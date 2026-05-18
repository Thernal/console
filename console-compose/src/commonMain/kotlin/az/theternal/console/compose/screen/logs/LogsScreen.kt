package az.theternal.console.compose.screen.logs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import az.theternal.console.compose.navigation.ConsoleLogObserver
import az.theternal.console.api.LocalLogRenderer

@Composable
internal fun LogsScreen(onNavigateToLogDetail: (groupId: String, logId: String) -> Unit) {
    val logs by ConsoleLogObserver.logs.collectAsState()
    val renderer = LocalLogRenderer.current

    LogsContent(
        logs = logs,
        onLogClick = { log -> onNavigateToLogDetail("", log.id) },
        renderer = renderer,
    )
}
