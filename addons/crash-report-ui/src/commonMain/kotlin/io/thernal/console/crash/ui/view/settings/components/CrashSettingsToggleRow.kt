package io.thernal.console.crash.ui.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsSwitch
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun CrashSettingsToggleRow(
    label: String,
    description: String?,
    checked: State<Boolean>,
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
            checked = checked.value,
            onCheckedChange = onCheckedChange,
        )
    }
}

@DsPreview
@Composable
private fun PreviewCrashSettingsToggleRow() {
    ThemeProvider {
        CrashSettingsToggleRow(
            label = "Show safe sessions",
            description = "List background and clean terminations too",
            checked = remember { mutableStateOf(false) },
            onCheckedChange = {},
        )
    }
}
