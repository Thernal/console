package az.theternal.console.core.details

import az.theternal.console.core.base.Log
import az.theternal.console.core.base.LogObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ConsoleLogObserver : LogObserver {

    private val _logs = MutableStateFlow<List<Log>>(emptyList())
    val logs: StateFlow<List<Log>> = _logs.asStateFlow()

    override suspend fun emit(event: Log) {
        _logs.update { it + event }
    }

    fun clear() {
        _logs.value = emptyList()
    }
}
