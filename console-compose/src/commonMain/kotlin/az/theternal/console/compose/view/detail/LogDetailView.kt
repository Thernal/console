package az.theternal.console.compose.view.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import az.theternal.console.api.navigation.LocalConsoleNavigator
import az.theternal.console.compose.ConsoleLogObserver
import az.theternal.console.api.ui.LocalLogRenderer

@Composable
internal fun LogDetailView(logId: String) {
    val navigator = LocalConsoleNavigator.current
    val logs by ConsoleLogObserver.logs.collectAsState()
    val log by remember(logId) { derivedStateOf { logs.find { it.id == logId } } }
    val renderer = LocalLogRenderer.current

    LogDetailContent(
        log = log,
        onBack = navigator::pop,
        renderer = renderer,
    )
}
