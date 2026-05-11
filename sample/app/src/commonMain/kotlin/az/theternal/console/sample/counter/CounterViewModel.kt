package az.theternal.console.sample.counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import az.theternal.console.core.Console
import az.theternal.console.core.base.Log
import az.theternal.console.core.base.LogLevel
import az.theternal.console.details.ConsoleDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CounterViewModel : ViewModel() {

    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment() {
        viewModelScope.launch {
            val prev = _count.value
            logAndUpdateDetails(
                action = "Incremented",
                from = prev,
                to = _count.value,
            )
            _count.value++
        }
    }

    fun decrement() {
        viewModelScope.launch {
            val prev = _count.value
            logAndUpdateDetails(
                action = "Decremented",
                from = prev,
                to = _count.value,
            )
            _count.value--
        }
    }

    fun reset() {
        viewModelScope.launch {
            val prev = _count.value
            logAndUpdateDetails(
                action = "Reset",
                from = prev,
                to = 0,
                logLevel = LogLevel.Error,
                tag = "RESET",
            )
            _count.value = 0
        }
    }

    private suspend fun logAndUpdateDetails(
        action: String,
        from: Int,
        to: Int,
        logLevel: LogLevel = LogLevel.Success,
        tag: String = "ACTION",
    ) {
        Console.asyncNotify {
            Log(
                message = "Counter $action: $from → $to",
                level = logLevel,
                tag = tag,
            )
        }
        ConsoleDetails.put("Counter" to to.toString())
        ConsoleDetails.put("Last Action" to action)
    }
}
