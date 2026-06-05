package io.thernal.console.stepper.compose.view.overlay.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import io.thernal.console.compose.core.ViewIntent

sealed interface StepperOverlayIntent : ViewIntent {
    data object ToggleExpanded : StepperOverlayIntent
    data class CardSizeChanged(
        val newSize: IntSize,
        val maxWidthPx: Float,
        val maxHeightPx: Float,
    ) : StepperOverlayIntent
    data class Dragged(
        val dragAmount: Offset,
        val maxWidthPx: Float,
        val maxHeightPx: Float,
    ) : StepperOverlayIntent
}
