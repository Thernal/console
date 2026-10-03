package io.thernal.console.inspector.ui.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow

/**
 * Full-screen layer that swallows touches and picks the node under the pointer: a tap or drag on
 * touch screens, plain hovering with a mouse.
 */
@Composable
internal fun InspectorPickLayer(onPick: (Offset, Boolean) -> Unit) {
    var origin by remember { mutableStateOf(Offset.Zero) }
    Box(
        Modifier
            .fillMaxSize()
            .onGloballyPositioned { origin = it.positionInWindow() }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: continue
                        val isPick = event.type == PointerEventType.Press || event.type == PointerEventType.Move
                        if (isPick) onPick(origin + change.position, event.type == PointerEventType.Press)
                        event.changes.forEach { it.consume() }
                    }
                }
            },
    )
}
