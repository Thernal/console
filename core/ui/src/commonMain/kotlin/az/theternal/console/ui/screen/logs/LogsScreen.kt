package az.theternal.console.ui.screen.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import az.theternal.console.ui.observer.ConsoleLogObserver
import az.theternal.console.addon.api.renderer.LocalLogRenderer
import az.theternal.console.ui.screen.logs.components.LogsEmptyState
import az.theternal.console.uikit.foundation.theme.Theme

@Composable
internal fun LogsScreen(onNavigateToLogDetail: (groupId: String, logId: String) -> Unit) {
    val logs by ConsoleLogObserver.logs.collectAsState()
    val renderer = LocalLogRenderer.current

    if (logs.isEmpty()) {
        LogsEmptyState()
        return
    }

    val reversedLogs = remember(logs) { logs.asReversed() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = Theme.dimens.dp8,
            bottom = Theme.dimens.dp16,
        ),
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
    ) {
        items(
            items = reversedLogs,
            key = { it.id },
            contentType = { "log_item" },
        ) { log ->
            renderer.Item(
                log = log,
                onClick = { onNavigateToLogDetail("", log.id) },
            )
        }
    }
}
