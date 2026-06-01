package az.theternal.console.stepper.compose.view.overlay.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import az.theternal.console.api.navigation.ConsoleRoute
import az.theternal.console.api.navigation.LocalConsoleNavigator
import az.theternal.console.api.ui.LocalLogRenderer
import az.theternal.console.designsystem.components.core.DsDivider
import az.theternal.console.designsystem.foundation.theme.Theme
import az.theternal.console.runtime.Log

@Composable
internal fun OverlayCurrentLogSection(
    isExpanded: State<Boolean>,
    currentLog: State<Log?>,
) {
    val visible = remember { derivedStateOf { isExpanded.value && currentLog.value != null } }

    AnimatedVisibility(
        visible = visible.value,
    ) {
        currentLog.value?.let { log ->
            OverlayCurrentLogContent(log = log)
        }
    }
}

@Composable
private fun OverlayCurrentLogContent(log: Log) {
    val navigator = LocalConsoleNavigator.current
    val renderer = LocalLogRenderer.current
    Column(modifier = Modifier.padding(horizontal = Theme.dimens.dp12)) {
        DsDivider(color = Theme.colors.border)
        renderer.Item(
            log = log,
            onClick = { navigator.push(ConsoleRoute.LogDetail("", log.id)) },
        )
        Spacer(Modifier.height(Theme.dimens.dp8))
    }
}
