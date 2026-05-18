package az.theternal.console.addon.api

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import az.theternal.console.addon.api.nav.ConsoleNavGraph
import az.theternal.console.addon.api.nav.ConsoleNavigation
import az.theternal.console.addon.api.overlay.ConsoleOverlays
import az.theternal.console.api.Console
import az.theternal.console.api.ConsoleScope

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
