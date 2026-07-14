package io.thernal.console.settings.ui.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.designsystem.components.core.DsTextField
import io.thernal.console.designsystem.foundation.theme.Theme

private const val DISABLED_ALPHA = 0.4f

/**
 * Clamps silently to `[min, max]` as the user types (rather than an inline error state) — the
 * simplest behavior that never leaves the field showing an invalid value.
 */
@Composable
internal fun SettingsIntFieldRow(
    title: String,
    description: String?,
    value: Int,
    min: Int,
    max: Int,
    enabled: Boolean,
    onValueChange: (Int) -> Unit,
) {
    var text by remember(value) { mutableStateOf(TextFieldValue(value.toString())) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else DISABLED_ALPHA)
            .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp12),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsRowLabel(title = title, description = description, modifier = Modifier.weight(1f))
        DsTextField(
            value = text,
            onValueChange = { new ->
                if (!enabled) return@DsTextField
                text = new
                val parsed = new.text.toIntOrNull() ?: return@DsTextField
                onValueChange(parsed.coerceIn(min, max))
            },
            modifier = Modifier.width(Theme.dimens.dp48),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
    }
}
