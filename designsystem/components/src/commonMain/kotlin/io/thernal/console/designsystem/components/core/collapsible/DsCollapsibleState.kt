package io.thernal.console.designsystem.components.core.collapsible

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Stable
@OptIn(ExperimentalMaterial3Api::class)
class DsCollapsibleState internal constructor(
    internal val topAppBarState: TopAppBarState,
) {
    internal var headerHeightPx by mutableFloatStateOf(0f)

    internal fun onHeaderMeasured(heightPx: Float) {
        if (heightPx == headerHeightPx) return
        headerHeightPx = heightPx
        topAppBarState.heightOffsetLimit = -heightPx
        topAppBarState.heightOffset = topAppBarState.heightOffset.coerceIn(-heightPx, 0f)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun rememberDsCollapsibleState(): DsCollapsibleState {
    val topAppBarState = rememberTopAppBarState()
    return remember(topAppBarState) { DsCollapsibleState(topAppBarState) }
}
