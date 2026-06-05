package io.thernal.console.stepper.compose.view.settings.model

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.compose.core.IntentHandler
import io.thernal.console.compose.core.StateHolder
import io.thernal.console.stepper.compose.stepper.Stepper
import io.thernal.console.stepper.compose.stepper.StepperIntent
import kotlinx.coroutines.launch

class StepperSettingsViewModel : ViewModel(), StateHolder, IntentHandler<StepperSettingsIntent> {
    val state = StepperSettingsState()

    init {
        syncFromConfig()
        viewModelScope.launch { Stepper.config.collect { syncFromConfig() } }
        viewModelScope.launch { Stepper.state.collect { state.steppedEvents.set(it.steppedEvents) } }
    }

    override val handler = onIntentUpdate { intent ->
        when (intent) {
            is StepperSettingsIntent.SetTagInput -> state.tagInput.update {
                intent.value.copy(text = intent.value.text)
            }

            StepperSettingsIntent.AddTag -> {
                val trimmed = state.tagInput.value.text.trim()
                if (trimmed.isNotEmpty()) {
                    Stepper.dispatch(StepperIntent.AddPauseTag(tag = trimmed))
                    state.tagInput.set(TextFieldValue())
                }
            }
        }
    }

    private fun syncFromConfig() {
        val config = Stepper.config.value
        snapshot {
            state.enabled.set(config.enabled)
            state.paused.set(config.paused)
            state.pauseOnMatch.set(config.pauseOnMatch)
            state.pauseOnTags.set(config.pauseOnTags)
            state.pauseOnLevelAtLeast.set(config.pauseOnLevelAtLeast)
            state.autoResumeSeconds.set(config.autoResumeSeconds)
        }
    }
}
