package az.theternal.console.addon.api.nav

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

interface ConsoleNavGraph {
    fun EntryProviderScope<NavKey>.routes() {}
}
