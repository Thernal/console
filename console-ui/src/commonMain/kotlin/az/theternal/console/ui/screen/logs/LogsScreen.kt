package az.theternal.console.ui.screen.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.ui.renderer.LocalLogRenderer
import az.theternal.console.ui.observer.ConsoleLogObserver
import az.theternal.console.ui.designsystem.foundation.theme.Theme
import az.theternal.console.ui.designsystem.components.core.DsIcon
import az.theternal.console.ui.designsystem.components.core.DsText

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

@Composable
private fun LogsEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
        ) {
            DsIcon(
                icon = Icons.Outlined.List,
                size = Theme.dimens.dp32,
                tint = Theme.colors.content04,
            )
            DsText(
                text = "No logs yet",
                style = Theme.typography.body02,
                color = Theme.colors.content03,
            )
            DsText(
                text = "Logs will appear here as they arrive",
                style = Theme.typography.body03,
                color = Theme.colors.content04,
            )
        }
    }
}
