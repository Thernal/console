package io.thernal.console.stepper.compose.view.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.thernal.console.api.navigation.ConsoleRoute
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.api.ui.LocalLogRenderer
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.runtime.Log
import io.thernal.console.runtime.LogLevel
import io.thernal.console.stepper.compose.navigation.SteppedEventsRoute
import io.thernal.console.stepper.compose.view.settings.model.StepperIntent

private const val CAUGHT_PREVIEW_COUNT = 3

@Composable
internal fun StepperCaughtSection(
    events: State<List<Log>>,
    dispatch: (StepperIntent) -> Unit,
) {
    val navigator = LocalConsoleNavigator.current
    val renderer = LocalLogRenderer.current
    val eventList = events.value
    val previewEvents = eventList.takeLast(CAUGHT_PREVIEW_COUNT).asReversed()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp10),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DsText(
                    text = "Caught",
                    style = Theme.typography.title02,
                    color = Theme.colors.content01,
                )
                Box(
                    modifier = Modifier
                        .clip(Theme.rounding.r4)
                        .background(Theme.colors.background3)
                        .padding(horizontal = Theme.dimens.dp6, vertical = Theme.dimens.dp4),
                ) {
                    DsText(
                        text = "${eventList.size}",
                        style = Theme.typography.label02,
                        color = Theme.colors.content02,
                    )
                }
            }
            DsText(
                text = "Clear",
                style = Theme.typography.label01,
                color = Theme.colors.danger,
                modifier = Modifier.pressable(
                    onPress = { dispatch(StepperIntent.ClearSteppedEvents) },
                ),
            )
        }

        previewEvents.forEach { log ->
            Box(
                modifier = Modifier.padding(
                    vertical = Theme.dimens.dp3,
                    horizontal = Theme.dimens.dp12,
                ),
            ) {
                renderer.Item(
                    log = log,
                    onClick = { navigator.push(ConsoleRoute.LogDetail("", log.id)) },
                )
            }
        }

        if (eventList.size > CAUGHT_PREVIEW_COUNT) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pressable(
                        onPress = { navigator.push(SteppedEventsRoute) },
                    )
                    .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp12),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DsText(
                    text = "See all ${eventList.size} events",
                    style = Theme.typography.label01,
                    color = Theme.colors.primary01,
                )
                DsIcon(
                    icon = Icons.AutoMirrored.Outlined.ArrowForward,
                    size = Theme.metrics.iconSm,
                    color = Theme.colors.primary01,
                )
            }
        }
    }
}

@DsPreview
@Composable
private fun PreviewStepperCaughtSection() {
    val events = remember {
        mutableStateOf(
            listOf(
                Log(
                    message = "Network request completed in 342ms",
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
                Log(
                    message = "Failed to decode response",
                    tag = "JSON",
                    level = LogLevel.Error,
                ),
            ),
        )
    }
    ThemeProvider {
        StepperCaughtSection(
            events = events,
            dispatch = {},
        )
    }
}
