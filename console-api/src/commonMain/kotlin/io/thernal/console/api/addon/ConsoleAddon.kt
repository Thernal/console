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

    /** Layered full-screen overlays, ordered and hidden together with the dock widget they name. */
    fun overlays(): List<ConsoleOverlay> {
        return emptyList()
    }

    /** A control for the shared floating dock; prefer it over [overlay] for anything interactive. */
    fun dockWidget(): ConsoleDockWidget? = null

    /**
     * Wraps the host app content (not the console UI). Use it to provide CompositionLocals the
     * app content must see. Must be registered before the first composition.
     */
    fun contentWrapper(): ConsoleContentWrapper? = null

    fun install() {
        onInstall()
        tab()?.let { ConsoleNavigation.registerTab(it) }
        navGraph()?.let { ConsoleNavigation.registerGraph(it) }
        overlay()?.let { ConsoleOverlays.register(it) }
        overlays().forEach { ConsoleOverlays.register(it) }
        dockWidget()?.let { ConsoleDock.register(it) }
        contentWrapper()?.let { ConsoleContentWrappers.register(it) }
    }
}
