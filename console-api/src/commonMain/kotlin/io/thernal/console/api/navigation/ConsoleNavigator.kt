package io.thernal.console.api.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavKey
import io.thernal.console.api.addon.ConsoleTab

interface ConsoleNavigator {
    fun push(key: NavKey)
    fun pop()
    fun close()
    fun openTab(graph: ConsoleTab? = null)
}

private object NoOpConsoleNavigator : ConsoleNavigator {
    override fun push(key: NavKey) {}
    override fun pop() {}
    override fun close() {}
    override fun openTab(graph: ConsoleTab?) {}
}

val LocalConsoleNavigator = staticCompositionLocalOf<ConsoleNavigator> { NoOpConsoleNavigator }
