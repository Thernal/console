package io.thernal.console.designsystem.components.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
fun DsSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier.size(
            width = Theme.dimens.dp44,
            height = Theme.dimens.dp24,
        ),
        colors = SwitchDefaults.colors(
            checkedThumbColor = Theme.colors.primaryContent,
            checkedTrackColor = Theme.colors.primary01,
            checkedBorderColor = Theme.colors.primary01,
            checkedIconColor = Theme.colors.primary01,
            uncheckedThumbColor = Theme.colors.content02,
            uncheckedTrackColor = Theme.colors.background3,
            uncheckedBorderColor = Theme.colors.border,
            uncheckedIconColor = Theme.colors.content03,
            disabledCheckedThumbColor = Theme.colors.content02,
            disabledCheckedTrackColor = Theme.colors.primary01.copy(alpha = 0.35f),
            disabledCheckedBorderColor = Theme.colors.primary01.copy(alpha = 0.35f),
            disabledCheckedIconColor = Theme.colors.content03,
            disabledUncheckedThumbColor = Theme.colors.content03,
            disabledUncheckedTrackColor = Theme.colors.background3.copy(alpha = 0.5f),
            disabledUncheckedBorderColor = Theme.colors.border.copy(alpha = 0.5f),
            disabledUncheckedIconColor = Theme.colors.content04,
        ),
    )
}

@DsPreview
@Composable
private fun PreviewDsSwitch() {
    ThemeProvider {
        Column(
            modifier = Modifier.padding(Theme.dimens.dp16),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
        ) {
            DsSwitch(checked = true, onCheckedChange = {})
            DsSwitch(checked = false, onCheckedChange = {})
        }
    }
}
