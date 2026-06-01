package az.theternal.console.stepper.compose.view.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.api.navigation.ConsoleRoute
import az.theternal.console.api.navigation.LocalConsoleNavigator
import az.theternal.console.api.ui.LocalLogRenderer
import az.theternal.console.compose.core.preview
import az.theternal.console.designsystem.components.core.DsAppBar
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.core.DsIconButton
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme
import az.theternal.console.runtime.Log
import az.theternal.console.runtime.LogLevel
import az.theternal.console.stepper.compose.view.events.model.SteppedEventsState

@Composable
internal fun SteppedEventsContent(state: SteppedEventsState) {
    val navigator = LocalConsoleNavigator.current

    Column(modifier = Modifier.fillMaxSize().background(Theme.colors.background1)) {
        DsAppBar(
            content = { SteppedEventsTitle(count = state.count) },
            leading = {
                DsIconButton(onClick = { navigator.pop() }) {
                    DsIcon(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        color = Theme.colors.content02,
                    )
                }
            },
        )
        SteppedEventsBody(events = state.events)
    }
}

@Composable
private fun SteppedEventsTitle(count: State<Int>) {
    DsText(
        text = "Caught (${count.value})",
        style = Theme.typography.title01,
        color = Theme.colors.content01,
    )
}

@Composable
private fun SteppedEventsBody(events: State<List<Log>>) {
    val navigator = LocalConsoleNavigator.current
    val renderer = LocalLogRenderer.current

    if (events.value.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DsIcon(
                    icon = Icons.Outlined.BugReport,
                    size = Theme.dimens.dp32,
                    color = Theme.colors.content04,
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
                start = Theme.dimens.dp12,
                end = Theme.dimens.dp12,
            ),
            verticalArrangement = Arrangement.spacedBy(space = Theme.dimens.dp8),
        ) {
            items(
                items = events.value,
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

@DsPreview
@Composable
private fun PreviewSteppedEventsContentEmpty() {
    ThemeProvider {
        SteppedEventsContent(state = SteppedEventsState())
    }
}

@DsPreview
@Composable
private fun PreviewSteppedEventsContentFilled() {
    val state = SteppedEventsState()
    state.count.preview(3)
    state.events.preview(
        listOf(
            Log(
                message = "Network request completed",
                tag = "HTTP",
                level = LogLevel.Info,
            ),
            Log(
                message = "Cache miss for key: user_profile",
                tag = "Cache",
                level = LogLevel.Debug,
            ),
            Log(
                message = "Auth token expired",
                tag = "Auth",
                level = LogLevel.Warning,
            ),
        ),
    )
    ThemeProvider {
        SteppedEventsContent(state = state)
    }
}
