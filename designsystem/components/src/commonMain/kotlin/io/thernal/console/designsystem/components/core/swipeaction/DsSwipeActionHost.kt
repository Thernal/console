package io.thernal.console.designsystem.components.core.swipeaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

/** Hosts a [SwipeActionCoordinator] so the [DsSwipeActionPane] rows beneath it close each other. */
@Composable
fun DsSwipeActionHost(
    coordinator: SwipeActionCoordinator = remember { SwipeActionCoordinator() },
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalSwipeActionCoordinator provides coordinator,
        content = content,
    )
}
