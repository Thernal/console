package io.thernal.console.settings.ui.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.foundation.theme.Theme

private const val DISABLED_ALPHA = 0.4f

/** [noneLabel] non-null renders an extra chip that clears the selection to `null`. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun <E : Enum<E>> SettingsEnumPickerRow(
    title: String,
    description: String?,
    options: List<E>,
    noneLabel: String?,
    selected: E?,
    enabled: Boolean,
    onSelect: (E?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else DISABLED_ALPHA)
            .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp8),
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
    ) {
        SettingsRowLabel(title = title, description = description)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
        ) {
            if (noneLabel != null) {
                DsChip(
                    label = noneLabel,
                    selected = selected == null,
                    modifier = Modifier.pressable(enabled = enabled, onPress = { onSelect(null) }),
                )
            }
            options.forEach { option ->
                DsChip(
                    label = option.name,
                    selected = option == selected,
                    modifier = Modifier.pressable(enabled = enabled, onPress = { onSelect(option) }),
                )
            }
        }
    }
}
