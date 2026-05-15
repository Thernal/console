package az.theternal.console.ui.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.ui.renderer.LocalLogRenderer
import az.theternal.console.ui.observer.ConsoleLogObserver
import az.theternal.console.ui.designsystem.foundation.theme.Theme
import az.theternal.console.ui.designsystem.components.core.DsIcon
import az.theternal.console.ui.designsystem.components.core.DsText

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
        LaunchedEffect(Unit) { onBack() }
        LogNotFoundState()
    }
}

@Composable
private fun LogNotFoundState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
        ) {
            DsIcon(
                icon = Icons.Outlined.SearchOff,
                size = Theme.dimens.dp32,
                tint = Theme.colors.content04,
            )
            DsText(
                text = "Log not found",
                style = Theme.typography.body02,
                color = Theme.colors.content03,
            )
        }
    }
}
