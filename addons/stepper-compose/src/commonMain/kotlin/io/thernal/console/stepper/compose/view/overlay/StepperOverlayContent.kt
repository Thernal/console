package io.thernal.console.stepper.compose.view.overlay

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.compose.core.preview
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.stepper.compose.stepper.StepperIntent
import io.thernal.console.stepper.compose.view.overlay.model.StepperOverlayIntent
import io.thernal.console.stepper.compose.view.overlay.model.StepperOverlayState
import io.thernal.console.stepper.compose.view.overlay.components.OverlayCard

@Composable
internal fun StepperOverlayContent(
    state: StepperOverlayState,
    dispatch: (StepperOverlayIntent) -> Unit,
    onStepperDispatch: (StepperIntent) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeContent),
    ) {
        OverlayCard(
            state = state,
            maxWidth = maxWidth,
            maxWidthPx = constraints.maxWidth.toFloat(),
            maxHeightPx = constraints.maxHeight.toFloat(),
            modifier = Modifier.align(Alignment.TopEnd),
            dispatch = dispatch,
            onStepperDispatch = onStepperDispatch,
        )
    }
}

@DsPreview
@Composable
private fun PreviewStepperOverlayContentDisabled() {
    ThemeProvider {
        StepperOverlayContent(
            state = StepperOverlayState(),
            dispatch = {},
            onStepperDispatch = {},
        )
    }
}

@DsPreview
@Composable
private fun PreviewStepperOverlayContentEnabled() {
    val state = StepperOverlayState().preview {
        state.isEnabled.set(true)
        state.isPaused.set(true)
        state.steppedEvents.set(
            listOf(
                io.thernal.console.runtime.log.Log(message = "Network request completed"),
                io.thernal.console.runtime.log.Log(message = "Cache miss"),
                io.thernal.console.runtime.log.Log(message = "Auth token expired"),
            ),
        )
        state.isExpanded.set(true)
    }
    ThemeProvider {
        StepperOverlayContent(
            state = state,
            dispatch = {},
            onStepperDispatch = {},
        )
    }
}
