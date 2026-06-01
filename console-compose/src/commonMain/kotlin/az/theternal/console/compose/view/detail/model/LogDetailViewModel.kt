package az.theternal.console.compose.view.detail.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import az.theternal.console.compose.ConsoleLogObserver
import az.theternal.console.compose.core.StateHolder
import kotlinx.coroutines.launch

class LogDetailViewModel(private val logId: String) : ViewModel(), StateHolder {
    val state = LogDetailState()

    init {
        viewModelScope.launch {
            ConsoleLogObserver.logs.collect { logs ->
                state.log.update { logs.find { it.id == logId } }
            }
        }
    }
}
