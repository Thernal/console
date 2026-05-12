package az.theternal.console.ui.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.core.Console
import az.theternal.console.ui.LocalLogRenderer
import az.theternal.console.ui.designsystem.foundation.theme.Theme
import az.theternal.console.ui.designsystem.components.core.DsText

@Composable
internal fun LogDetailScreen(
    logId: String,
    onBack: () -> Unit,
) {
    val logs by Console.logObserver.logs.collectAsState()
    val log = logs.find { it.id == logId }
    val renderer = LocalLogRenderer.current

    if (log == null) {
        LaunchedEffect(Unit) { onBack() }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            DsText("Log not found", style = Theme.typography.body02, color = Theme.colors.content03)
        }
        return
    }

    renderer.Detail(log = log, onBack = onBack)
}
