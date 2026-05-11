package az.theternal.console.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavKey

interface ConsoleNavigator {
    fun push(key: NavKey)
    fun pop()
    fun openTab(tabTitle: String? = null)
}

val LocalConsoleNavigator = compositionLocalOf<ConsoleNavigator?> { null }
