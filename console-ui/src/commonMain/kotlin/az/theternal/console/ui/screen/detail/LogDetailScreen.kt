package az.theternal.console.ui.screen.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import az.theternal.console.ui.observer.ConsoleLogObserver
import az.theternal.console.ui.renderer.LocalLogRenderer
import az.theternal.console.ui.screen.detail.components.LogNotFoundState

@Composable
internal fun LogDetailScreen(
    logId: String,
    onBack: () -> Unit,
) {
    val logs by ConsoleLogObserver.logs.collectAsState()
    val log by remember(logId) { derivedStateOf { logs.find { it.id == logId } } }
    val renderer = LocalLogRenderer.current

    log?.let { currentLog ->
        renderer.Detail(log = currentLog, onBack = onBack)
    } ?: run {
        LaunchedEffect(onBack) { onBack() }
        LogNotFoundState()
    }
}
