package io.thernal.console.compose.trigger

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import io.thernal.console.api.trigger.ConsoleTrigger
import io.thernal.console.api.trigger.KeyModifier

private fun KeyEvent.matchesModifiers(modifiers: Set<KeyModifier>): Boolean {
    return modifiers.all { modifier ->
        when (modifier) {
            KeyModifier.Ctrl -> isCtrlPressed
            KeyModifier.Alt -> isAltPressed
            KeyModifier.Shift -> isShiftPressed
            KeyModifier.Meta -> isMetaPressed
        }
    }
}

fun ConsoleTrigger.Companion.keyboardShortcut(
    key: Key,
    modifiers: Set<KeyModifier> = emptySet(),
): ConsoleTrigger = ConsoleTrigger { onDetected ->
    onPreviewKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
        if (event.key != key) return@onPreviewKeyEvent false
        if (!event.matchesModifiers(modifiers)) return@onPreviewKeyEvent false
        onDetected()
        true
    }
}
