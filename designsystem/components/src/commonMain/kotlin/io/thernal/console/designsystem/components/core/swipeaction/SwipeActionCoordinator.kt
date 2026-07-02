package io.thernal.console.designsystem.components.core.swipeaction

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Coordinates the open panes of a list: at most one row keeps its actions revealed at a time —
 * opening a pane closes the previously focused one. Provided by [DsSwipeActionHost].
 */
@Stable
class SwipeActionCoordinator {

    var focusedItemId by mutableStateOf<Any?>(null)
        private set

    fun updateFocused(id: Any?) {
        focusedItemId = id
    }

    fun clearFocus() {
        focusedItemId = null
    }
}

val LocalSwipeActionCoordinator = staticCompositionLocalOf<SwipeActionCoordinator?> { null }
