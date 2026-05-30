package az.theternal.console.stepper.compose.view.settings.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import az.theternal.console.api.navigation.ConsoleRoute
import az.theternal.console.api.navigation.LocalConsoleNavigator
import az.theternal.console.api.ui.LocalLogRenderer
import az.theternal.console.compose.core.ViewState
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.components.modifier.pressable
import az.theternal.console.designsystem.foundation.theme.Theme
import az.theternal.console.stepper.DebugStepper
import az.theternal.console.stepper.compose.navigation.SteppedEventsRoute
import az.theternal.console.stepper.compose.view.settings.DebugStepperIntent

private const val CAUGHT_PREVIEW_COUNT = 3

@Composable
internal fun StepperCaughtSection(
    stepperState: ViewState.StateField<DebugStepper.State>,
    dispatch: (DebugStepperIntent) -> Unit,
) {
    val navigator = LocalConsoleNavigator.current
    val renderer = LocalLogRenderer.current
    val events = stepperState.value.steppedEvents
    val preview = events.takeLast(CAUGHT_PREVIEW_COUNT).asReversed()

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
                        text = "${events.size}",
                        style = Theme.typography.label02,
                        color = Theme.colors.content02,
                    )
                }
            }
            DsText(
                text = "Clear",
                style = Theme.typography.label01,
                color = Theme.colors.danger,
                modifier = Modifier.pressable(onPress = { dispatch(DebugStepperIntent.ClearSteppedEvents) }),
            )
        }

        preview.forEach { log ->
            Box(modifier = Modifier.padding(vertical = Theme.dimens.dp3, horizontal = Theme.dimens.dp12)) {
                renderer.Item(
                    log = log,
                    onClick = { navigator.push(ConsoleRoute.LogDetail("", log.id)) },
                )
            }
        }

        if (events.size > CAUGHT_PREVIEW_COUNT) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pressable(onPress = { navigator.push(SteppedEventsRoute) })
                    .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp12),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DsText(
                    text = "See all ${events.size} events",
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
