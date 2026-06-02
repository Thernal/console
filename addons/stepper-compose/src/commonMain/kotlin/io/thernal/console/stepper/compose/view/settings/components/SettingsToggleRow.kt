package io.thernal.console.stepper.compose.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsSwitch
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun SettingsToggleRow(
    label: String,
    description: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp12),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = Theme.dimens.dp16),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp2),
        ) {
            DsText(
                text = label,
                style = Theme.typography.body01,
                color = Theme.colors.content01,
            )
            if (description != null) {
                DsText(
                    text = description,
                    style = Theme.typography.body03,
                    color = Theme.colors.content04,
                )
            }
        }
        DsSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@DsPreview
@Composable
private fun PreviewSettingsToggleRow() {
    ThemeProvider {
        SettingsToggleRow(
            label = "Active",
            description = "Enable or disable the stepper",
            checked = true,
            onCheckedChange = {},
        )
    }
}
