package az.theternal.console.stepper.compose.view.settings.model

import androidx.compose.runtime.snapshots.Snapshot
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
        syncFromConfig()
        viewModelScope.launch { Stepper.config.collect { syncFromConfig() } }
        viewModelScope.launch { Stepper.state.collect { state.steppedEvents.set(it.steppedEvents) } }
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
                    state.tagInput.set(TextFieldValue())
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

    private fun syncFromConfig() {
        val config = Stepper.config.value
        Snapshot.withMutableSnapshot {
            state.enabled.set(config.enabled)
            state.paused.set(config.paused)
            state.pauseOnMatch.set(config.pauseOnMatch)
            state.pauseOnTags.set(config.pauseOnTags)
            state.pauseOnLevelAtLeast.set(config.pauseOnLevelAtLeast)
            state.autoResumeSeconds.set(config.autoResumeSeconds)
            state.isStepperActive.set(config.enabled && config.paused)
        }
    }
}
