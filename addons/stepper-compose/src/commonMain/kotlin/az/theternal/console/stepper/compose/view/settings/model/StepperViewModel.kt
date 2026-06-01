package az.theternal.console.stepper.compose.view.settings.model

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import az.theternal.console.compose.core.IntentHandler
import az.theternal.console.compose.core.StateHolder
import az.theternal.console.stepper.Stepper
import kotlinx.coroutines.launch

class StepperViewModel : ViewModel(), StateHolder, IntentHandler<StepperIntent> {
    val state = StepperSettingsState()

    init {
        viewModelScope.launch {
            Stepper.config.collect { config ->
                state.enabled.update { config.enabled }
                state.paused.update { config.paused }
                state.pauseOnMatch.update { config.pauseOnMatch }
                state.pauseOnTags.update { config.pauseOnTags }
                state.pauseOnLevelAtLeast.update { config.pauseOnLevelAtLeast }
                state.autoResumeSeconds.update { config.autoResumeSeconds }
                state.isStepperActive.update { config.enabled && config.paused }
            }
        }
        viewModelScope.launch {
            Stepper.state.collect { stepperState ->
                state.steppedEvents.update { stepperState.steppedEvents }
            }
        }
    }

    override val handler = Handler { intent ->
        when (intent) {
            is StepperIntent.SetTagInput -> state.tagInput.update {
                intent.value.copy(text = intent.value.text.uppercase())
            }

            is StepperIntent.SetEnabled -> Stepper.updateConfig { copy(enabled = intent.enabled) }

            is StepperIntent.SetPaused -> Stepper.updateConfig { copy(paused = intent.paused) }

            is StepperIntent.SetPauseOnMatch ->
                Stepper.updateConfig { copy(pauseOnMatch = intent.pauseOnMatch) }

            StepperIntent.AddTag -> {
                val trimmed = state.tagInput.value.text.trim()
                if (trimmed.isNotEmpty()) {
                    Stepper.updateConfig { copy(pauseOnTags = pauseOnTags + trimmed) }
                    state.tagInput.update { TextFieldValue() }
                }
            }

            is StepperIntent.RemovePauseTag ->
                Stepper.updateConfig { copy(pauseOnTags = pauseOnTags - intent.tag) }

            is StepperIntent.SetPauseOnLevelAtLeast ->
                Stepper.updateConfig { copy(pauseOnLevelAtLeast = intent.level) }

            is StepperIntent.SetAutoResumeSeconds ->
                Stepper.updateConfig { copy(autoResumeSeconds = intent.seconds) }

            StepperIntent.ClearSteppedEvents -> Stepper.clearSteppedEvents()
        }
    }
}
