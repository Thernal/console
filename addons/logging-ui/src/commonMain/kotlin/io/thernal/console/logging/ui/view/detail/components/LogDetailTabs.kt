package io.thernal.console.logging.ui.view.detail.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import io.thernal.console.ui.common.LocalSearchQuery
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.core.tabview.DsTabState
import io.thernal.console.designsystem.components.core.tabview.DsTabView
import io.thernal.console.designsystem.components.modifier.applyIf
import io.thernal.console.designsystem.components.modifier.focusRing
import io.thernal.console.runtime.log.Log

@Composable
fun LogDetailTabs(
    tabState: DsTabState,
    logs: State<List<Log>>,
) {
    val tabs = logs.value

    if (tabs.size > 1) {
        DsTabView(
            state = tabState,
            modifier = Modifier.fillMaxWidth(),
            tab = { tabItemState ->
                val tabLog = tabs.getOrNull(tabItemState.index) ?: return@DsTabView

                val containsQuery = tabLog.contains(LocalSearchQuery.current.value)

                DsChip(
                    modifier = Modifier
                        .applyIf(containsQuery) {
                            focusRing()
                        },
                    label = tabLog.tab,
                    selected = tabItemState.isSelected,
                )
            },
        )
    }
}
