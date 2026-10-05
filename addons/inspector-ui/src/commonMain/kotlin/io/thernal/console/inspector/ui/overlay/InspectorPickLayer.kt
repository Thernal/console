package io.thernal.console.inspector.ui.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow

/**
 * Full-screen layer that swallows touches and picks the node under the pointer. A press is a tap pick; once the
 * pointer moves past the touch slop while pressed, every move is a drag pick. Hovering a mouse picks nothing, so it
 * never undoes a tap that stepped into a component.
 */
@Composable
internal fun InspectorPickLayer(onPick: (point: Offset, isPress: Boolean) -> Unit) {
    var origin by remember { mutableStateOf(Offset.Zero) }
    val currentOnPick by rememberUpdatedState(onPick)
    Box(
        Modifier
            .fillMaxSize()
            .onGloballyPositioned { origin = it.positionInWindow() }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    var pressedAt: Offset? = null
                    var isDragging = false
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: continue
                        when (event.type) {
                            PointerEventType.Press -> {
                                pressedAt = change.position
                                isDragging = false
                                currentOnPick(origin + change.position, true)
                            }

                            PointerEventType.Move -> {
                                val start = pressedAt
                                if (change.pressed && start != null) {
                                    val moved = (change.position - start).getDistance()
                                    if (moved > viewConfiguration.touchSlop) isDragging = true
                                    if (isDragging) currentOnPick(origin + change.position, false)
                                }
                            }

                            PointerEventType.Release -> pressedAt = null
                        }
                        event.changes.forEach { it.consume() }
                    }
                }
            },
    )
}
