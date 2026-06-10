package io.thernal.console.logging.ui.view.logs.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.ui.core.select
import io.thernal.console.logging.ui.view.logs.model.LogsIntent
import io.thernal.console.logging.ui.view.logs.model.LogsState
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.components.core.DsTextField
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun LogsSearchBar(
    state: LogsState,
    dispatch: (LogsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    DsTextField(
        value = state.searchQuery.value,
        onValueChange = { dispatch(LogsIntent.SetQuery(it)) },
        modifier = modifier,
        hint = "Search logs…",
        prefix = {
            DsIcon(
                icon = Icons.Outlined.Search,
                size = Theme.metrics.iconMd,
                color = Theme.colors.content04,
                modifier = Modifier.padding(end = Theme.dimens.dp8),
            )
        },
        suffix = {
            AnimatedVisibility(
                visible = state.searchQuery.select { it.text.isNotEmpty() }.value,
            ) {
                DsIconButton(
                    onClick = { dispatch(LogsIntent.SetQuery(TextFieldValue())) },
                    contentColor = Theme.colors.content04,
                ) {
                    DsIcon(
                        icon = Icons.Outlined.Clear,
                        size = Theme.metrics.iconMd,
                    )
                }
            }
        },
    )
}

@DsPreview
@Composable
private fun PreviewLogsSearchBar() {
    ThemeProvider {
        LogsSearchBar(state = LogsState(), dispatch = {})
    }
}
