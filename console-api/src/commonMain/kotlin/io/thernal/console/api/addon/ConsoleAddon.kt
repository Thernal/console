package io.thernal.console.api.addon

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable

interface ConsoleAddon {
    /**
     * Hook for data-plane registration (observers/processors). Addons that capture logs
     * reference the `Console` singleton (console-runtime) directly here; view-only
     * addons leave it empty. Kept Console-free so this module stays independent of
     * console-runtime.
     */
    fun onInstall() {}
    fun tab(): ConsoleTab? = null
    fun navGraph(): ConsoleNavGraph? = null
    fun overlay(): (@Composable BoxScope.() -> Unit)? = null

    fun install() {
        onInstall()
        tab()?.let { ConsoleNavigation.registerTab(it) }
        navGraph()?.let { ConsoleNavigation.registerGraph(it) }
        overlay()?.let { ConsoleOverlays.register(it) }
    }
}
