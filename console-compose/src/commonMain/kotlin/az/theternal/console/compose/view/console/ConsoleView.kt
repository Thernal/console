package az.theternal.console.compose.view.console

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import az.theternal.console.api.addon.ConsoleNavigation
import az.theternal.console.api.addon.ConsoleTab

@Composable
internal fun ConsoleView(
    onClose: () -> Unit,
    requestedTab: ConsoleTab? = null,
    onRequestConsumed: () -> Unit = {},
) {
    val tabs = ConsoleNavigation.tabs
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val safeIndex = selectedIndex.coerceIn(0, (tabs.size - 1).coerceAtLeast(0))

    LaunchedEffect(requestedTab) {
        if (requestedTab != null) {
            val idx = tabs.indexOf(requestedTab)
            if (idx >= 0) selectedIndex = idx
            onRequestConsumed()
        }
    }

    ConsoleContent(
        tabs = tabs,
        selectedIndex = safeIndex,
        onTabSelect = { selectedIndex = it },
        onClose = onClose,
    )
}
