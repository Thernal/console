package az.theternal.console.api.addon

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import az.theternal.console.runtime.Console
import az.theternal.console.runtime.ConsoleScope

interface ConsoleAddon {
    fun onInstall(console: ConsoleScope) {}
    fun tab(): ConsoleTab? = null
    fun navGraph(): ConsoleNavGraph? = null
    fun overlay(): (@Composable BoxScope.() -> Unit)? = null

    fun install(console: ConsoleScope = Console) {
        onInstall(console)
        tab()?.let { ConsoleNavigation.registerTab(it) }
        navGraph()?.let { ConsoleNavigation.registerGraph(it) }
        overlay()?.let { ConsoleOverlays.register(it) }
    }
}
