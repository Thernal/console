package az.theternal.console.api

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import az.theternal.console.api.ConsoleNavGraph
import az.theternal.console.api.ConsoleNavigation
import az.theternal.console.api.ConsoleOverlays
import az.theternal.console.runtime.Console
import az.theternal.console.runtime.ConsoleScope

interface ConsoleAddon {
    fun onInstall(console: ConsoleScope)
    fun navGraph(): ConsoleNavGraph? {
        return null
    }
    fun overlay(): (@Composable BoxScope.() -> Unit)? = null

    fun install(console: ConsoleScope = Console) {
        onInstall(console)
        navGraph()?.let { ConsoleNavigation.register(it) }
        overlay()?.let { ConsoleOverlays.register(it) }
    }
}
