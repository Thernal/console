package io.thernal.console.stepper.ui.view.overlay.model

import androidx.compose.runtime.Stable
import io.thernal.console.ui.core.ViewState
import io.thernal.console.ui.core.derive
import io.thernal.console.core.log.Log

@Stable
class StepperOverlayState : ViewState() {
    val isEnabled = field(false)
    val isPaused = field(false)
    val pendingLogs = field(0)
    val blockedLogId = field<String?>(null)
    val blockedTag = field<String?>(null)
    val steppedEvents = field(emptyList<Log>())

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
}
