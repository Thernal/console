package io.thernal.console.sample.counter

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.runtime.Console
import io.thernal.console.runtime.Log
import io.thernal.console.runtime.LogLevel
import io.thernal.console.details.ConsoleDetails
import kotlinx.coroutines.launch

class CounterViewModel : ViewModel() {

    private val _count = mutableIntStateOf(0)
    val count: State<Int> = _count

    fun increment() {
        viewModelScope.launch {
            logAndUpdateDetails(
                action = "Incremented",
                from = count.value,
                to = ++_count.value,
            )
        }
    }

    fun decrement() {
        viewModelScope.launch {
            logAndUpdateDetails(
                action = "Decremented",
                from = count.value,
                to = --_count.value,
            )
        }
    }

    fun logMultiline() {
        viewModelScope.launch {
            Console.asyncNotify {
                Log(
                    message = "POST /api/v2/session\n" +
                        "Status: 200 OK\n" +
                        "Body: {\n" +
                        "  \"userId\": 42,\n" +
                        "  \"token\": \"eyJhbGciOiJIUzI1NiJ9...\",\n" +
                        "  \"expiresIn\": 3600\n" +
                        "}",
                    level = LogLevel.Info,
                    tag = "HTTP",
                )
            }
        }
    }

    fun reset() {
        viewModelScope.launch {
            logAndUpdateDetails(
                action = "Reset",
                from = count.value,
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
