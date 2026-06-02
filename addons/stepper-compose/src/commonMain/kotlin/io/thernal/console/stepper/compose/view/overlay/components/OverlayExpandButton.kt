package io.thernal.console.stepper.compose.view.overlay.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.stepper.compose.view.overlay.model.StepperOverlayIntent

@Composable
internal fun OverlayExpandSection(
    isEnabled: State<Boolean>,
    isExpanded: State<Boolean>,
    dispatch: (StepperOverlayIntent) -> Unit,
) {
    if (!isEnabled.value) return
    OverlayExpandButton(
        isExpanded = isExpanded,
        dispatch = dispatch,
    )
}

@Composable
internal fun OverlayExpandButton(
    isExpanded: State<Boolean>,
    dispatch: (StepperOverlayIntent) -> Unit,
) {
    OverlayIconButton(onClick = { dispatch(StepperOverlayIntent.ToggleExpanded) }) {
        DsIcon(
            icon = if (isExpanded.value) Icons.Outlined.KeyboardArrowDown else Icons.Outlined.KeyboardArrowUp,
            size = Theme.metrics.iconMd,
            color = Theme.colors.content04,
        )
    }
}
