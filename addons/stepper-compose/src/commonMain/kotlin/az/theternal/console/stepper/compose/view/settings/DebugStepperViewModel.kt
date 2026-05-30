package az.theternal.console.stepper.compose.view.settings

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import az.theternal.console.compose.core.IntentHandler
import az.theternal.console.compose.core.StateHolder
import az.theternal.console.stepper.DebugStepper
import kotlinx.coroutines.launch

class DebugStepperViewModel : ViewModel(), StateHolder, IntentHandler<DebugStepperIntent> {
    override val state = DebugStepperSettingsState()

    init {
        viewModelScope.launch { DebugStepper.config.collect { state.config.update { it } } }
        viewModelScope.launch { DebugStepper.state.collect { state.stepperState.update { it } } }
    }

    override val handler = Handler { intent ->
        when (intent) {
            is DebugStepperIntent.SetTagInput -> state.tagInput.update {
                intent.value.copy(text = intent.value.text.uppercase())
            }

            is DebugStepperIntent.SetEnabled -> DebugStepper.updateConfig { copy(enabled = intent.enabled) }

            is DebugStepperIntent.SetPaused -> DebugStepper.updateConfig { copy(paused = intent.paused) }

            is DebugStepperIntent.SetPauseOnMatch ->
                DebugStepper.updateConfig { copy(pauseOnMatch = intent.pauseOnMatch) }

            DebugStepperIntent.AddTag -> {
                val trimmed = state.tagInput.value.text.trim()
                if (trimmed.isNotEmpty()) {
                    DebugStepper.updateConfig { copy(pauseOnTags = pauseOnTags + trimmed) }
                    state.tagInput.update { TextFieldValue() }
                }
            }

            is DebugStepperIntent.RemovePauseTag ->
                DebugStepper.updateConfig { copy(pauseOnTags = pauseOnTags - intent.tag) }

            is DebugStepperIntent.SetPauseOnLevelAtLeast ->
                DebugStepper.updateConfig { copy(pauseOnLevelAtLeast = intent.level) }

            is DebugStepperIntent.SetAutoResumeSeconds ->
                DebugStepper.updateConfig { copy(autoResumeSeconds = intent.seconds) }

            DebugStepperIntent.ClearSteppedEvents -> DebugStepper.clearSteppedEvents()
        }
    }
}
