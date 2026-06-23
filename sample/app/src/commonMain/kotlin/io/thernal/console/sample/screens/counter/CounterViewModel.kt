package io.thernal.console.sample.screens.counter

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import io.thernal.console.runtime.console.Console
import kotlinx.coroutines.launch

class CounterViewModel : ViewModel() {

    private val _count = mutableStateOf(0)
    val count: State<Int> = _count

    fun increment() {
        change(by = 1)
    }

    fun decrement() {
        change(by = -1)
    }

    fun reset() {
        if (_count.value == 0) return
        _count.value = 0
        log(LogLevel.Warning, "Counter reset to 0")
    }

    private fun change(by: Int) {
        _count.value += by
        log(LogLevel.Info, "Counter changed to ${_count.value}")
    }

    private fun log(
        level: LogLevel,
        message: String,
    ) {
        viewModelScope.launch {
            Console.asyncNotify {
                Log(message = message, level = level, tag = "Counter")
            }
        }
    }
}
