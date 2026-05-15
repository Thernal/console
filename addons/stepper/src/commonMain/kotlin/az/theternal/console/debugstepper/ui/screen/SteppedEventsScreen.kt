package az.theternal.console.debugstepper.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.debugstepper.DebugStepper
import az.theternal.console.ui.designsystem.components.core.DsIcon
import az.theternal.console.ui.designsystem.components.core.DsIconButton
import az.theternal.console.ui.designsystem.components.core.DsText
import az.theternal.console.ui.designsystem.components.core.DsTopAppBar
import az.theternal.console.ui.designsystem.foundation.theme.Theme
import az.theternal.console.ui.nav.ConsoleRoute
import az.theternal.console.ui.nav.LocalConsoleNavigator
import az.theternal.console.ui.renderer.LocalLogRenderer

@Composable
internal fun SteppedEventsScreen() {
    val navigator = LocalConsoleNavigator.current
    val renderer = LocalLogRenderer.current
    val state by DebugStepper.state.collectAsState()
    val events = remember(state.steppedEvents) { state.steppedEvents.asReversed() }

    Column(modifier = Modifier.fillMaxSize().background(Theme.colors.background1)) {
        DsTopAppBar(
            title = {
                DsText(
                    text = "Caught (${state.steppedEvents.size})",
                    style = Theme.typography.title01,
                    color = Theme.colors.content01,
                )
            },
            navigationIcon = {
                DsIconButton(onClick = { navigator.pop() }) {
                    DsIcon(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Theme.colors.content02,
                    )
                }
            },
        )

        if (events.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    DsIcon(
                        icon = Icons.Outlined.BugReport,
                        size = Theme.dimens.dp32,
                        tint = Theme.colors.content04,
                    )
                    DsText(
                        text = "Nothing caught yet",
                        style = Theme.typography.body02,
                        color = Theme.colors.content03,
                        modifier = Modifier.padding(top = Theme.dimens.dp8),
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = Theme.dimens.dp8,
                    bottom = Theme.dimens.dp16,
                ),
            ) {
                items(
                    items = events,
                    key = { it.id },
                    contentType = { "log_item" },
                ) { log ->
                    renderer.Item(
                        log = log,
                        onClick = { navigator.push(ConsoleRoute.LogDetail("", log.id)) },
                    )
                }
            }
        }
    }
}
