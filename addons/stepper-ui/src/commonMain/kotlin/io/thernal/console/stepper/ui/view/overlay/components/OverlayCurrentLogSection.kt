package io.thernal.console.stepper.ui.view.overlay.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.thernal.console.api.navigation.ConsoleRoute
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.api.ui.LocalLogRenderer
import io.thernal.console.designsystem.components.core.DsDivider
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.runtime.log.Log

@Composable
internal fun OverlayCurrentLogSection(
    isExpanded: State<Boolean>,
    currentLog: State<Log?>,
) {
    val visible = remember { derivedStateOf { isExpanded.value && currentLog.value != null } }

    AnimatedVisibility(
        visible = visible.value,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkOut(),
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
            modifier = Modifier.pressable(
                onPress = {
                    navigator.push(
                        key = ConsoleRoute.LogDetail(logId = log.id),
                    )
                },
            ),
        )
        Spacer(Modifier.height(Theme.dimens.dp8))
    }
}
