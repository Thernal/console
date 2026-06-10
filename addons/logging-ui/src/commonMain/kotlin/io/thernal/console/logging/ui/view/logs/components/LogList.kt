package io.thernal.console.logging.ui.view.logs.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import io.thernal.console.api.ui.LogRenderer
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.runtime.log.Log

@Composable
fun ColumnScope.LogList(
    logs: State<List<Log>>,
    renderer: LogRenderer,
    onLogClick: (Log) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(
            top = Theme.dimens.dp8,
            bottom = Theme.dimens.dp16,
            start = Theme.dimens.dp12,
            end = Theme.dimens.dp12,
        ),
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
    ) {
        items(
            items = logs.value,
            key = { it.id },
            contentType = { "log_item" },
        ) { log ->
            renderer.Item(
                log = log,
                modifier = Modifier.pressable(
                    onPress = { onLogClick(log) },
                ),
            )
        }
    }
}
