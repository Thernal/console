package io.thernal.console.settings.ui.view.settings.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.settings.SettingsRegistry
import io.thernal.console.ui.core.IntentHandler
import io.thernal.console.ui.core.StateHolder
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel(), StateHolder, IntentHandler<SettingsIntent> {
    val state = SettingsState()

    init {
        viewModelScope.launch {
            SettingsRegistry.sections.collect { state.sections.set(it) }
        }
    }

    override val handler = onIntentUpdate { intent ->
        when (intent) {
            is SettingsIntent.SetQuery -> state.searchQuery.set(intent.query)
        }
    }
}
