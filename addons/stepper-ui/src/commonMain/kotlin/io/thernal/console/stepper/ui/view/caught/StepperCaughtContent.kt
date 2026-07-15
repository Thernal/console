package io.thernal.console.stepper.ui.view.caught

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.api.ui.LocalLogRenderer
import io.thernal.console.core.log.Log
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.stepper.ui.navigation.SteppedLogRoute
import io.thernal.console.stepper.ui.view.caught.components.StepperCaughtEmptyState

@Composable
internal fun StepperCaughtContent(steppedEvents: State<List<Log>>) {
    if (steppedEvents.value.isEmpty()) {
        StepperCaughtEmptyState()
        return
    }

    val navigator = LocalConsoleNavigator.current
    val renderer = LocalLogRenderer.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = Theme.dimens.dp8,
            bottom = Theme.dimens.dp16,
            start = Theme.dimens.dp12,
            end = Theme.dimens.dp12,
        ),
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
    ) {
        items(
            items = steppedEvents.value,
            key = { it.id },
            contentType = { "log_item" },
        ) { log ->
            renderer.Item(
                log = log,
                modifier = Modifier.pressable(
                    onPress = { navigator.push(key = SteppedLogRoute(logId = log.id)) },
                ),
            )
        }
    }
}
