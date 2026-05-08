package az.theternal.console.ui.nav

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey

class ConsoleNavController internal constructor(
    private val backStack: SnapshotStateList<NavKey>,
    private val onClose: () -> Unit,
) {
    fun navigate(key: NavKey) {
        backStack.add(key)
    }

    fun popBack() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        } else {
            onClose()
        }
    }
}

val LocalConsoleNavController = compositionLocalOf<ConsoleNavController> {
    error("No ConsoleNavController provided — make sure ConsoleInstaller() is in the composition tree")
}
