package io.thernal.console.settings.ui.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.ui.common.highlight

/**
 * Plain action, or a destructive one requiring a two-tap confirm (first tap arms — label turns to
 * the danger color and prompts to tap again — the second tap within the armed state runs it).
 */
@Composable
internal fun SettingsActionRow(
    title: String,
    destructive: Boolean = false,
    onClick: () -> Unit,
) {
    var isArmed by remember { mutableStateOf(false) }
    val isConfirming = destructive && isArmed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressable(
                onPress = {
                    if (destructive && !isArmed) {
                        isArmed = true
                    } else {
                        onClick()
                        isArmed = false
                    }
                },
            )
            .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp12),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = (if (isConfirming) "Tap again to confirm" else title).highlight(),
            style = Theme.typography.body01,
            color = if (isConfirming) Theme.colors.danger else Theme.colors.content01,
        )
    }
}
