package io.thernal.console.settings.ui.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import io.thernal.console.designsystem.components.core.DsSwitch
import io.thernal.console.designsystem.foundation.theme.Theme

private const val DISABLED_ALPHA = 0.4f

/**
 * [checked]/[enabled] are [State] so reads happen as late and as narrowly as possible:
 * [enabled]'s dimming is a `graphicsLayer` block, evaluated at draw time — this composable's own
 * body never reads it, so it never recomposes for that reason. The label is static (no state).
 * Only [ToggleSwitch] below reads `checked`/`enabled` at composition time, since [DsSwitch]
 * genuinely needs both together to render — that's the row's sole, unavoidable state read.
 */
@Composable
internal fun SettingsToggleRow(
    title: String,
    description: String?,
    checked: State<Boolean>,
    enabled: State<Boolean>,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = if (enabled.value) 1f else DISABLED_ALPHA }
            .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp8),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsRowLabel(title = title, description = description, modifier = Modifier.weight(1f))
        ToggleSwitch(checked = checked, enabled = enabled, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ToggleSwitch(
    checked: State<Boolean>,
    enabled: State<Boolean>,
    onCheckedChange: (Boolean) -> Unit,
) {
    val isChecked by checked
    val isEnabled by enabled
    DsSwitch(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        enabled = isEnabled,
    )
}
