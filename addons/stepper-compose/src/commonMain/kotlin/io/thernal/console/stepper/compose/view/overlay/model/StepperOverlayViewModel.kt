package io.thernal.console.stepper.compose.view.overlay.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.compose.core.IntentHandler
import io.thernal.console.compose.core.StateHolder
import io.thernal.console.stepper.compose.stepper.Stepper
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class StepperOverlayViewModel : ViewModel(), StateHolder, IntentHandler<StepperOverlayIntent> {
    val state = StepperOverlayState()

    init {
        syncDerived()
        viewModelScope.launch {
            combine(Stepper.config, Stepper.state) { _, _ -> Unit }.collect { syncDerived() }
        }
    }

    override val handler = onIntentUpdate { intent ->
        when (intent) {
            StepperOverlayIntent.ToggleExpanded -> {
                if (state.isEnabled.value) {
                    state.isExpanded.update { !this }
                }
            }

            is StepperOverlayIntent.CardSizeChanged -> updateCardSize(
                newSize = intent.newSize,
                maxWidthPx = intent.maxWidthPx,
                maxHeightPx = intent.maxHeightPx,
            )

            is StepperOverlayIntent.Dragged -> updateDragOffset(
                dragAmount = intent.dragAmount,
                maxWidthPx = intent.maxWidthPx,
                maxHeightPx = intent.maxHeightPx,
            )
        }
    }

    private fun updateCardSize(
        newSize: IntSize,
        maxWidthPx: Float,
        maxHeightPx: Float,
    ) {
        snapshot {
            state.cardSize.set(newSize)
            state.offsetX.set(
                state.offsetX.value.coerceIn(
                    minimumValue = -(maxWidthPx - newSize.width).coerceAtLeast(0f),
                    maximumValue = 0f,
                ),
            )
            state.offsetY.set(
                state.offsetY.value.coerceIn(
                    minimumValue = 0f,
                    maximumValue = (maxHeightPx - newSize.height).coerceAtLeast(0f),
                ),
            )
        }
    }

    private fun updateDragOffset(
        dragAmount: Offset,
        maxWidthPx: Float,
        maxHeightPx: Float,
    ) {
        val cardSize = state.cardSize.value

        snapshot {
            state.offsetX.set(
                (state.offsetX.value + dragAmount.x).coerceIn(
                    minimumValue = -(maxWidthPx - cardSize.width).coerceAtLeast(0f),
                    maximumValue = 0f,
                ),
            )
            state.offsetY.set(
                (state.offsetY.value + dragAmount.y).coerceIn(
                    minimumValue = 0f,
                    maximumValue = (maxHeightPx - cardSize.height).coerceAtLeast(0f),
                ),
            )
        }
    }

    private fun syncDerived() {
        val config = Stepper.config.value
        val stepperState = Stepper.state.value

        snapshot {
            if (!config.enabled) state.isExpanded.set(false)
            state.isEnabled.set(config.enabled)
            state.isPaused.set(config.paused)
            state.pendingLogs.set(stepperState.pendingLogs)
            state.blockedLogId.set(stepperState.blockedLogId)
            state.blockedTag.set(stepperState.blockedTag)
            state.steppedEvents.set(stepperState.steppedEvents)
        }
    }
}
