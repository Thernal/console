package az.theternal.console.compose.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import az.theternal.console.api.navigation.ConsoleNavigator
import az.theternal.console.api.navigation.ConsoleRoute
import az.theternal.console.api.addon.ConsoleTab

@Stable
internal class ConsoleNavigatorImpl(
    private val backStack: SnapshotStateList<NavKey>,
    private val consoleVisible: MutableState<Boolean>,
    private val requestedTab: MutableState<ConsoleTab?>,
) : ConsoleNavigator {

    override fun push(key: NavKey) {
        consoleVisible.value = true
        backStack.add(key)
    }

    override fun pop() {
        if (backStack.size > 2) {
            backStack.removeLastOrNull()
        } else {
            close()
        }
    }

    override fun openTab(graph: ConsoleTab?) {
        consoleVisible.value = true
        requestedTab.value = graph
    }

    override fun close() {
        consoleVisible.value = false
        requestedTab.value = null
        backStack.clear()
        backStack.addAll(listOf(ConsoleRoute.Stub, ConsoleRoute.Main))
    }
}
