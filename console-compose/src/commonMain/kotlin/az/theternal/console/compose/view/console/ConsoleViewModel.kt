package az.theternal.console.compose.view.console

import androidx.lifecycle.ViewModel
import az.theternal.console.api.addon.ConsoleNavigation
import az.theternal.console.compose.core.IntentHandler
import az.theternal.console.compose.core.StateHolder

class ConsoleViewModel : ViewModel(), StateHolder, IntentHandler<ConsoleIntent> {
    override val state = ConsoleState()

    override val handler = Handler { intent ->
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
