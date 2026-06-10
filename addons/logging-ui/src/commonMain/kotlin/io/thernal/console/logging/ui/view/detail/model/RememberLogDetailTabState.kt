package io.thernal.console.logging.ui.view.detail.model

import androidx.compose.runtime.Composable
import io.thernal.console.designsystem.components.core.tabview.DsTabState
import io.thernal.console.designsystem.components.core.tabview.rememberDsTabState

@Composable
fun rememberLogDetailTabState(state: LogDetailState): DsTabState? {
    if (!state.isInitialized.value) {
        return null
    }

    val tabs = state.logs.value

    if (tabs.isEmpty()) {
        return null
    }

    return rememberDsTabState(
        itemCount = tabs.size,
        initialPage = state.selectedPageIndex.value,
    )
}
