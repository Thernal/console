package io.thernal.console.settings.ui.view.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsTextField
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun SettingsSearchBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
) {
    DsTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        hint = "Search settings…",
        prefix = {
            DsIcon(
                icon = Icons.Outlined.Search,
                size = Theme.metrics.iconSm,
                color = Theme.colors.content04,
                modifier = Modifier.padding(end = Theme.dimens.dp8),
            )
        },
        suffix = {
            AnimatedVisibility(visible = query.text.isNotEmpty()) {
                DsIcon(
                    icon = Icons.Outlined.Clear,
                    size = Theme.metrics.iconSm,
                    color = Theme.colors.content04,
                    modifier = Modifier.pressable(onPress = { onQueryChange(TextFieldValue()) }),
                )
            }
        },
    )
}
