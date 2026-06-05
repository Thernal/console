package io.thernal.console.stepper.compose.view.overlay.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntSize
import io.thernal.console.compose.core.ViewState
import io.thernal.console.compose.core.derive
import io.thernal.console.runtime.log.Log

@Stable
class StepperOverlayState : ViewState() {
    val isEnabled = field(false)
    val isPaused = field(false)
    val pendingLogs = field(0)
    val blockedLogId = field<String?>(null)
    val blockedTag = field<String?>(null)
    val steppedEvents = field(emptyList<Log>())
    val isExpanded = field(false)
    val offsetX = field(0f)
    val offsetY = field(0f)
    val cardSize = field(IntSize.Zero)

    val currentLog = steppedEvents.derive { currentEvents ->
        currentEvents.lastOrNull()
    }

    val displayTag = isEnabled.derive { isStepperEnabled ->
        if (!isStepperEnabled) {
            null
        } else {
            blockedTag.value ?: currentLog.value?.tag
        }
    }

    val canStep = blockedLogId.derive { id ->
        id != null
    }

    val caughtCount = steppedEvents.derive { currentEvents ->
        currentEvents.size
    }

    val statusText = isEnabled.derive { isStepperEnabled ->
        when {
            !isStepperEnabled -> "Disabled"
            isPaused.value && canStep.value -> "Paused"
            isPaused.value && pendingLogs.value == 0 -> "Idle"
            isPaused.value -> "Running · ${pendingLogs.value} queued"
            else -> "Running"
        }
    }

    val statusTone = isEnabled.derive { isStepperEnabled ->
        when {
            !isStepperEnabled -> StepperStatusTone.Disabled
            isPaused.value && canStep.value -> StepperStatusTone.Paused
            isPaused.value -> StepperStatusTone.Idle
            else -> StepperStatusTone.Running
        }
    }
}
