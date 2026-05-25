package az.theternal.console.compose.view.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import az.theternal.console.api.ui.LogRenderer
import az.theternal.console.compose.view.logs.components.LogsEmptyState
import az.theternal.console.designsystem.foundation.theme.Theme
import az.theternal.console.runtime.Log

@Composable
internal fun LogsContent(
    logs: List<Log>,
    onLogClick: (Log) -> Unit,
    renderer: LogRenderer,
) {
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
            start = Theme.dimens.dp12,
            end = Theme.dimens.dp12,
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
                onClick = { onLogClick(log) },
            )
        }
    }
}
