package io.thernal.console.sample.screens.playground

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import io.thernal.console.details.ConsoleDetails
import io.thernal.console.network.NetworkLog
import io.thernal.console.runtime.console.Console
import io.thernal.console.sample.network.FakeTodoRepository
import kotlinx.coroutines.launch

class PlaygroundViewModel : ViewModel() {

    private val repository = FakeTodoRepository()

    private val _isFetchingTodo = mutableStateOf(false)
    val isFetchingTodo: State<Boolean> = _isFetchingTodo

    private val _isCreatingPost = mutableStateOf(false)
    val isCreatingPost: State<Boolean> = _isCreatingPost

    private var jsonDemoCounter = 0

    // Console.notify is fire-and-forget — order is irrelevant for a single independent log.
    fun logAtLevel(level: LogLevel) {
        Console.notify {
            Log(message = "Log at ${level.name.lowercase()} level", level = level, tag = "Demo")
        }
    }

    // asyncNotify suspends until each event is processed — order is guaranteed across the run.
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

    // Network requests are captured automatically by the Ktor network addon — no extra code.
    fun fetchTodo() {
        if (_isFetchingTodo.value) return
        viewModelScope.launch {
            _isFetchingTodo.value = true
            runCatching { repository.fetchTodo() }
            _isFetchingTodo.value = false
        }
    }

    fun createPost() {
        if (_isCreatingPost.value) return
        viewModelScope.launch {
            _isCreatingPost.value = true
            runCatching { repository.createPost() }
            _isCreatingPost.value = false
        }
    }

    // Emits a synthetic network log with a minified JSON body so the detail view's JSON
    // pretty-printing can be shown without depending on a live response.
    fun logUnformattedJson() {
        val groupId = "json-demo-${jsonDemoCounter++}"
        val url = "https://api.example.com/v1/echo"

        Console.notify {
            NetworkLog.Request(
                method = "POST",
                url = url,
                headers = jsonHeaders(DEMO_REQUEST_JSON),
                body = DEMO_REQUEST_JSON,
                groupId = groupId,
            )
        }

        Console.notify {
            NetworkLog.Response(
                method = "POST",
                url = url,
                headers = jsonHeaders(DEMO_RESPONSE_JSON),
                body = DEMO_RESPONSE_JSON,
                statusCode = DEMO_STATUS_OK,
                durationMs = DEMO_DURATION_MS,
                level = LogLevel.Success,
                groupId = groupId,
            )
        }
    }

    // Thrown uncaught on purpose: the crash-report addon's handler appends the fatal record,
    // finalizes the session file, then chains to the platform default so the app still dies.
    // After relaunch the session appears in the console's Crashes tab.
    fun crash(): Nothing {
        error("Manual crash from the Playground")
    }

    private fun jsonHeaders(body: String): Map<String, String> {
        return mapOf(
            "Content-Type" to "application/json",
            "Content-Length" to body.length.toString(),
        )
    }

    override fun onCleared() {
        repository.close()
        super.onCleared()
    }

    companion object {
        const val ORDERED_LOG_COUNT = 5

        private const val DEMO_STATUS_OK = 200
        private const val DEMO_DURATION_MS = 12L

        private const val DEMO_REQUEST_JSON =
            """{"title":"console sample post","body":"unformatted single-line json",""" +
                """"userId":42,"tags":["network","json"]}"""

        private const val DEMO_RESPONSE_JSON =
            """{"id":42,"name":"Console Sample","active":true,""" +
                """"tags":["network","json","kmp"],""" +
                """"meta":{"createdAt":"2026-06-17T10:00:00Z","views":1280,""" +
                """"nested":{"a":1,"b":[1,2,3],"c":null}},""" +
                """"items":[{"k":"v1"},{"k":"v2"}]}"""
    }
}
