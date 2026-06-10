package io.thernal.console.details.ui.view.details.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.ui.core.StateHolder
import io.thernal.console.details.ConsoleDetails
import kotlinx.coroutines.launch

class DetailsViewModel : ViewModel(), StateHolder {
    val state = DetailsState()

    init {
        viewModelScope.launch {
            ConsoleDetails.flow.collect { state.details.set(it) }
        }
    }
}
