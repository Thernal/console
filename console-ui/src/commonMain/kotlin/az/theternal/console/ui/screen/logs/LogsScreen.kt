package az.theternal.console.ui.screen.logs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.runtime.Console
import az.theternal.console.ui.LocalLogRenderer
import az.theternal.console.ui.designsystem.foundation.theme.Theme
import az.theternal.console.ui.designsystem.components.core.DsText

@Composable
internal fun LogsScreen(onNavigateToLogDetail: (groupId: String, logId: String) -> Unit) {
    val logs by Console.logObserver.logs.collectAsState()
    val renderer = LocalLogRenderer.current

    if (logs.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            DsText("No logs yet", style = Theme.typography.body02, color = Theme.colors.content03)
        }
        return
    }

    val reversedLogs = remember(logs) { logs.asReversed() }

    LazyColumn(Modifier.fillMaxSize()) {
        items(reversedLogs, key = { it.id }) { log ->
            renderer.Item(
                log = log,
                onClick = { onNavigateToLogDetail("", log.id) },
            )
        }
    }
}
