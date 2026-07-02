package io.thernal.console.ui.logdetail.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.ui.core.select
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.components.core.DsTextField
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
fun LogDetailSearchField(
    searchQuery: State<TextFieldValue>,
    onChange: (TextFieldValue) -> Unit,
) {
    DsTextField(
        value = searchQuery.value,
        onValueChange = { onChange(it) },
        hint = "Search in detail…",
        prefix = {
            DsIcon(
                icon = Icons.Outlined.Search,
                size = Theme.metrics.iconMd,
                color = Theme.colors.content04,
                modifier = Modifier.padding(end = Theme.dimens.dp8),
            )
        },
        suffix = {
            ClearButton(
                searchQuery = searchQuery,
                onChange = onChange,
            )
        },
    )
}

@Composable
private fun ClearButton(
    searchQuery: State<TextFieldValue>,
    onChange: (TextFieldValue) -> Unit,
) {
    val hasQuery = searchQuery.select { it.text.isNotEmpty() }

    if (hasQuery.value) {
        DsIconButton(
            onClick = { onChange(TextFieldValue()) },
            contentColor = Theme.colors.content04,
        ) {
            DsIcon(
                icon = Icons.Outlined.Clear,
                size = Theme.metrics.iconMd,
            )
        }
    }
}
