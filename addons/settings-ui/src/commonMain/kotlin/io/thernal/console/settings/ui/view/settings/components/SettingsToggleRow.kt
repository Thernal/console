package io.thernal.console.settings.ui.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import io.thernal.console.designsystem.components.core.DsSwitch
import io.thernal.console.designsystem.foundation.theme.Theme

private const val DISABLED_ALPHA = 0.4f

@Composable
internal fun SettingsToggleRow(
    title: String,
    description: String?,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else DISABLED_ALPHA)
            .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp12),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsRowLabel(title = title, description = description, modifier = Modifier.weight(1f))
        DsSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
        )
    }
}
