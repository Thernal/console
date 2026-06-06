package io.thernal.console.sample.counter

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.details.ConsoleDetails
import io.thernal.console.runtime.console.Console
import io.thernal.console.runtime.log.Log
import io.thernal.console.runtime.log.LogLevel
import io.thernal.console.sample.network.FakeTodoRepository
import kotlinx.coroutines.launch

class SampleViewModel : ViewModel() {

    private val repository = FakeTodoRepository()

    private val _isFetchingTodo = mutableStateOf(false)
    val isFetchingTodo: State<Boolean> = _isFetchingTodo

    private val _isCreatingPost = mutableStateOf(false)
    val isCreatingPost: State<Boolean> = _isCreatingPost

    // Console.notify is fire-and-forget — safe to call from any thread or context
    fun logAtLevel(level: LogLevel) {
        Console.notify {
            Log(
                message = "Log at ${level.name.lowercase()} level",
                level = level,
                tag = "Demo",
            )
        }
    }

    // asyncNotify suspends until each event is processed — order is guaranteed across all callers
    fun logOrdered() {
        viewModelScope.launch {
            repeat(times = ORDERED_LOG_COUNT) { index ->
                Console.asyncNotify {
                    Log(
                        message = "Step ${index + 1} of $ORDERED_LOG_COUNT",
                        level = LogLevel.Info,
                        tag = "Ordering",
                    )
                }
            }
        }
    }

    // ConsoleDetails.put upserts a key — visible in the Details tab inside the console
    fun setSessionInfo() {
        ConsoleDetails.put("User" to "alice@example.com")
        ConsoleDetails.put("Env" to "staging")
        ConsoleDetails.put("Build" to "1.0.0-debug")
    }

    fun clearDetails() {
        ConsoleDetails.remove("User")
        ConsoleDetails.remove("Env")
        ConsoleDetails.remove("Build")
    }

    // Network requests are captured automatically by ConsoleNetworkKtorPlugin — no extra code needed
    fun fetchTodo() {
        if (isFetchingTodo.value) return
        viewModelScope.launch {
            _isFetchingTodo.value = true
            runCatching { repository.fetchTodo() }
            _isFetchingTodo.value = false
        }
    }

    fun createPost() {
        if (isCreatingPost.value) return
        viewModelScope.launch {
            _isCreatingPost.value = true
            runCatching { repository.createPost() }
            _isCreatingPost.value = false
        }
    }

    override fun onCleared() {
        repository.close()
        super.onCleared()
    }

    companion object {
        const val ORDERED_LOG_COUNT = 5
    }
}
