package az.theternal.console.ui.nav

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

interface ConsoleNavGraph {
    fun EntryProviderScope<NavKey>.routes() {}
}
