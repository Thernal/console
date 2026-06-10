package io.thernal.console.ui.view.console.model

import androidx.lifecycle.ViewModel
import io.thernal.console.api.addon.ConsoleNavigation
import io.thernal.console.ui.core.IntentHandler
import io.thernal.console.ui.core.StateHolder

class ConsoleViewModel : ViewModel(), StateHolder, IntentHandler<ConsoleIntent> {
    val state = ConsoleState()

    override val handler = onIntentUpdate { intent ->
        when (intent) {
            is ConsoleIntent.SelectTab -> selectTab(intent.index)

            is ConsoleIntent.RequestTab -> {
                if (intent.tab != null) {
                    val idx = ConsoleNavigation.tabs.indexOf(intent.tab)
                    if (idx >= 0) state.selectedIndex.update { idx }
                }
            }
        }
    }

    private fun selectTab(index: Int) {
        state.selectedIndex.update {
            index.coerceIn(0, (ConsoleNavigation.tabs.size - 1).coerceAtLeast(0))
        }
    }
}
