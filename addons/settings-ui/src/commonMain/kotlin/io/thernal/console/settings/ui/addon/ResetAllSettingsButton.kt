package io.thernal.console.settings.ui.addon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsButton
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.ui.common.highlight

/**
 * The General section's own row: a two-tap destructive [DsButton] (first tap arms — the label
 * prompts to tap again — the second tap within the armed state runs [onClick]). Owns its
 * arm/confirm state; the caller only supplies [onClick].
 */
@Composable
internal fun ResetAllSettingsButton(onClick: () -> Unit) {
    var isArmed by remember { mutableStateOf(false) }

    DsButton(
        onClick = {
            if (isArmed) {
                onClick()
                isArmed = false
            } else {
                isArmed = true
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp8),
        color = Theme.colors.danger,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        DsText(
            text = (if (isArmed) "Tap again to confirm" else "Reset all settings").highlight(),
            style = Theme.typography.body01,
        )
        DsIcon(icon = Icons.Outlined.Delete, size = Theme.metrics.iconMd)
    }
}
