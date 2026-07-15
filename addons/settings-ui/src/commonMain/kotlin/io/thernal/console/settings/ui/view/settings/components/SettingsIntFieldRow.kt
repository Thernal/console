package io.thernal.console.settings.ui.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.designsystem.components.core.DsTextField
import io.thernal.console.designsystem.foundation.theme.Theme

private const val DISABLED_ALPHA = 0.4f

/**
 * Clamps silently to `[min, max]` (rather than an inline error state) — the simplest behavior that
 * never leaves the persisted config holding an invalid value. The clamp is only ever applied to
 * what gets persisted, never to what's on screen while the field has focus: forcing the displayed
 * text to the clamped bound mid-edit (e.g. while backspacing through a longer number) would fight
 * every keystroke that transiently dips outside the range. The display corrects to the persisted,
 * clamped value once the field loses focus.
 *
 * [value]/[enabled] are [State] rather than plain values so the read happens right here, at the
 * leaf — the caller can pass the same `State` reference across recompositions without ever
 * reading `.value` itself, keeping it un-recomposed when a sibling row's value changes.
 *
 * This composable's own body reads only [value] — genuinely needed here to seed/reconcile the
 * local text state above. [enabled] is read only inside the `graphicsLayer` block (draw time) and
 * the `onValueChange` callback (event time), never at composition time, so toggling `enabled`
 * alone never recomposes this row's text-editing machinery.
 */
@Composable
internal fun SettingsIntFieldRow(
    title: String,
    description: String?,
    value: State<Int>,
    min: Int,
    max: Int,
    enabled: State<Boolean>,
    onValueChange: (Int) -> Unit,
) {
    var text by remember { mutableStateOf(TextFieldValue(value.value.toString())) }
    var isOwnEdit by remember { mutableStateOf(false) }

    fun resyncToValue() {
        if (text.text.toIntOrNull() != value.value) {
            text = TextFieldValue(text = value.value.toString(), selection = TextRange(value.value.toString().length))
        }
    }

    // Skip the resync when `value` only just changed because of our own edit below — otherwise
    // this would clobber the cursor position (or the in-progress digits) we just set, snapping the
    // field to the clamped bound on every keystroke instead of only once focus is lost.
    LaunchedEffect(value.value) {
        if (isOwnEdit) {
            isOwnEdit = false
        } else {
            resyncToValue()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = if (enabled.value) 1f else DISABLED_ALPHA }
            .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp8),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsRowLabel(title = title, description = description, modifier = Modifier.weight(1f))
        DsTextField(
            value = text,
            onValueChange = { new ->
                if (!enabled.value) return@DsTextField
                val digitsOnly = new.text.filter { it.isDigit() }
                text = TextFieldValue(text = digitsOnly, selection = TextRange(digitsOnly.length))
                val parsed = digitsOnly.toIntOrNull() ?: return@DsTextField
                isOwnEdit = true
                onValueChange(parsed.coerceIn(min, max))
            },
            // Wide enough for a 5-digit value (e.g. a 65_536 max) plus the field's own padding.
            modifier = Modifier
                .width(Theme.dimens.dp48 + Theme.dimens.dp32)
                .onFocusChanged { if (!it.isFocused) resyncToValue() },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
    }
}
