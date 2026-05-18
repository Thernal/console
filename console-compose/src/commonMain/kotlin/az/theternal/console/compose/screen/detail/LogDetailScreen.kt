package az.theternal.console.compose.screen.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import az.theternal.console.compose.navigation.ConsoleLogObserver
import az.theternal.console.api.LocalLogRenderer

@Composable
internal fun LogDetailScreen(
    logId: String,
    onBack: () -> Unit,
) {
    val logs by ConsoleLogObserver.logs.collectAsState()
    val log by remember(logId) { derivedStateOf { logs.find { it.id == logId } } }
    val renderer = LocalLogRenderer.current

    if (log == null) LaunchedEffect(onBack) { onBack() }

    LogDetailContent(
        log = log,
        onBack = onBack,
        renderer = renderer,
    )
}
