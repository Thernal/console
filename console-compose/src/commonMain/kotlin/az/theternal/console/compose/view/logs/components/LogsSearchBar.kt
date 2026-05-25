package az.theternal.console.compose.view.logs.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.core.DsIconButton
import az.theternal.console.designsystem.components.core.DsTextField
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun LogsSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    DsTextField(
        value = query,
        onValueChange = onQueryChange,
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
            if (query.isNotEmpty()) {
                DsIconButton(
                    onClick = { onQueryChange("") },
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
