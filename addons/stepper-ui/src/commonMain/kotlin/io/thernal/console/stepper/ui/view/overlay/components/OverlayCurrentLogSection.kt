package io.thernal.console.stepper.ui.view.overlay.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.stepper.ui.navigation.SteppedLogRoute
import io.thernal.console.api.ui.LocalLogRenderer
import io.thernal.console.designsystem.components.core.DsDivider
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.core.log.Log

@Composable
internal fun OverlayCurrentLogSection(currentLog: State<Log?>) {
    currentLog.value?.let { log -> OverlayCurrentLogContent(log = log) }
}

@Composable
private fun OverlayCurrentLogContent(log: Log) {
    val navigator = LocalConsoleNavigator.current
    val renderer = LocalLogRenderer.current
    Column {
        DsDivider(color = Theme.colors.border)
        renderer.Item(
            log = log,
            modifier = Modifier.pressable(
                onPress = {
                    navigator.push(
                        key = SteppedLogRoute(logId = log.id),
                    )
                },
            ),
        )
        Spacer(Modifier.height(Theme.dimens.dp8))
    }
}
