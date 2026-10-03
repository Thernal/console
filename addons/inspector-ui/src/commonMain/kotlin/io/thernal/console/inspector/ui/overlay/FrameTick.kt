package io.thernal.console.inspector.ui.overlay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos

/** A value that changes every frame while [isActive], so draw-phase reads of live layout data stay current. */
@Composable
internal fun rememberFrameTick(isActive: Boolean): State<Long> {
    val tick = remember { mutableLongStateOf(0L) }
    LaunchedEffect(isActive) {
        if (!isActive) return@LaunchedEffect
        while (true) {
            withFrameNanos { tick.longValue = it }
        }
    }
    return tick
}
