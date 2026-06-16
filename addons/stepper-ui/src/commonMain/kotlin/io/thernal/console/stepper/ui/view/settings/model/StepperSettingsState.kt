package io.thernal.console.stepper.ui.view.settings.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.ui.core.ViewState
import io.thernal.console.ui.core.derive
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel

@Stable
class StepperSettingsState : ViewState() {
    val enabled = field(false)
    val paused = field(false)
    val pauseOnMatch = field(false)
    val pauseOnTags = field(emptySet<String>())
    val pauseOnLevelAtLeast = field<LogLevel?>(null)
    val autoResumeSeconds = field<Int?>(null)
    val steppedEvents = field(emptyList<Log>())
    val tagInput = field(TextFieldValue())

    val isStepperActive = enabled.derive { isEnabled ->
        isEnabled && paused.value
    }
}
