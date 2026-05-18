package az.theternal.console.compose.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import az.theternal.console.api.ConsoleNavigator
import az.theternal.console.api.ConsoleRoute
import az.theternal.console.api.ConsoleTab

internal class ConsoleNavigatorImpl(
    private val consoleVisibleState: MutableState<Boolean>,
    private val requestedTabState: MutableState<ConsoleTab?>,
) : ConsoleNavigator {

    val backStack: SnapshotStateList<NavKey> =
        mutableStateListOf(ConsoleRoute.Stub, ConsoleRoute.Main)

    override fun push(key: NavKey) {
        consoleVisibleState.value = true
        backStack.add(key)
    }

    override fun pop() {
        if (backStack.size > 2) backStack.removeLastOrNull() else close()
    }

    override fun openTab(graph: ConsoleTab?) {
        consoleVisibleState.value = true
        requestedTabState.value = graph
    }

    fun close() {
        consoleVisibleState.value = false
        requestedTabState.value = null
        backStack.clear()
        backStack.addAll(listOf(ConsoleRoute.Stub, ConsoleRoute.Main))
    }
}
