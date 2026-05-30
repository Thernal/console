package az.theternal.console.details.compose.view.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import az.theternal.console.compose.core.StateHolder
import az.theternal.console.details.ConsoleDetails
import kotlinx.coroutines.launch

class DetailsViewModel : ViewModel(), StateHolder {
    override val state = DetailsState()

    init {
        viewModelScope.launch {
            ConsoleDetails.flow.collect { state.details.update { it } }
        }
    }
}
