package az.theternal.console.ui.nav

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavKey

interface ConsoleNavigator {
    fun push(key: NavKey)
    fun pop()
    fun openTab(graph: ConsoleTab? = null)
}

private object NoOpConsoleNavigator : ConsoleNavigator {
    override fun push(key: NavKey) {}
    override fun pop() {}
    override fun openTab(graph: ConsoleTab?) {}
}

val LocalConsoleNavigator = staticCompositionLocalOf<ConsoleNavigator> { NoOpConsoleNavigator }
