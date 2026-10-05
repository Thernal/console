package io.thernal.console.ui.dock

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntSize
import io.thernal.console.ui.core.ViewState

@Stable
internal class DockState : ViewState() {
    /** The widget whose tab is showing. Null, or an id that no longer exists, means the first widget. */
    val selectedId = field<String?>(null)
    val isMinimized = field(true)
    val isDragging = field(false)
    val size = field(IntSize.Zero)
    val offsetX = field(0f)
    val offsetY = field(0f)
}
