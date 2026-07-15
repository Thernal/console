package io.thernal.console.settings.ui.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.foundation.theme.Theme

private const val DISABLED_ALPHA = 0.4f

/**
 * [noneLabel] non-null renders an extra chip that clears the selection to `null`.
 *
 * [selected]/[enabled] are [State]. [selected] is genuinely read at composition time here — every
 * chip's own `selected` flag depends on it, so this row recomposes when it changes; that's this
 * row's one unavoidable state read. [enabled]'s dimming is deferred to a `graphicsLayer` block
 * (draw time); its only composition-time use is gating each chip's own `pressable`, which is
 * already inside this same per-row scope (chips can't be rendered without reading `selected`
 * anyway), so reading it inline there adds no further recomposition cost.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun <E : Enum<E>> SettingsEnumPickerRow(
    title: String,
    description: String?,
    options: List<E>,
    noneLabel: String?,
    selected: State<E?>,
    enabled: State<Boolean>,
    onSelect: (E?) -> Unit,
) {
    val selectedValue by selected
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = if (enabled.value) 1f else DISABLED_ALPHA }
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
                    selected = selectedValue == null,
                    modifier = Modifier.pressable(enabled = enabled.value, onPress = { onSelect(null) }),
                )
            }
            options.forEach { option ->
                DsChip(
                    label = option.name,
                    selected = option == selectedValue,
                    modifier = Modifier.pressable(enabled = enabled.value, onPress = { onSelect(option) }),
                )
            }
        }
    }
}
