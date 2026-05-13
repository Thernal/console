package az.theternal.console.debugstepper

import az.theternal.console.runtime.model.Log
import az.theternal.console.runtime.model.LogLevel
import az.theternal.console.runtime.api.LogObserver
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.concurrent.Volatile

object DebugStepper : LogObserver {

    private const val MAX_STEPPED_EVENTS = 50
    private const val SECONDS_TO_MILLIS = 1_000L

    private val _state = MutableStateFlow(DebugStepperState())
    val state: StateFlow<DebugStepperState> = _state.asStateFlow()

    @Volatile
    private var currentWaiter: CompletableDeferred<Unit>? = null

    // ── Public controls ───────────────────────────────────────────────────────

    fun setEnabled(value: Boolean) {
        _state.update { it.copy(enabled = value) }
        if (!value) releaseCurrentWaiter()
    }

    fun setPaused(value: Boolean) {
        _state.update { it.copy(paused = value) }
        if (!value) releaseCurrentWaiter()
    }

    fun setPauseOnMatch(value: Boolean) {
        _state.update { it.copy(pauseOnMatch = value) }
    }

    fun addPauseTag(tag: String) {
        _state.update { it.copy(pauseOnTags = it.pauseOnTags + tag) }
    }

    fun removePauseTag(tag: String) {
        _state.update { it.copy(pauseOnTags = it.pauseOnTags - tag) }
    }

    fun setPauseOnLevelAtLeast(level: LogLevel?) {
        _state.update { it.copy(pauseOnLevelAtLeast = level) }
    }

    fun setAutoResumeSeconds(seconds: Int?) {
        _state.update { it.copy(autoResumeSeconds = seconds) }
    }

    fun clearSteppedEvents() {
        _state.update { it.copy(steppedEvents = emptyList()) }
    }

    fun next() {
        releaseCurrentWaiter()
    }

    // ── LogObserver ───────────────────────────────────────────────────────────

    override suspend fun emit(event: Log) {
        _state.update { it.copy(pendingLogs = it.pendingLogs + 1) }
        try {
            awaitIfNeeded(event)
        } finally {
            _state.update { it.copy(pendingLogs = (it.pendingLogs - 1).coerceAtLeast(0)) }
        }
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private suspend fun awaitIfNeeded(event: Log) {
        if (!_state.value.enabled || !_state.value.paused) return
        if (!shouldPause(event)) return

        val waiter = CompletableDeferred<Unit>()
        currentWaiter = waiter

        if (!_state.value.enabled || !_state.value.paused || waiter.isCompleted) {
            currentWaiter = null
            return
        }

        _state.update {
            it.copy(
                blockedLogId = event.id,
                blockedTag = event.tag,
                steppedEvents = (it.steppedEvents + event).takeLast(MAX_STEPPED_EVENTS),
            )
        }
        try {
            val timeoutMs = _state.value.autoResumeSeconds?.let { it.toLong() * SECONDS_TO_MILLIS }
            if (timeoutMs != null) {
                withTimeoutOrNull(timeoutMs) { waiter.await() }
            } else {
                waiter.await()
            }
        } finally {
            currentWaiter = null
            _state.update { it.copy(blockedLogId = null, blockedTag = null) }
        }
    }

    private fun shouldPause(event: Log): Boolean {
        val state = _state.value
        if (!state.pauseOnMatch) return true
        if (state.pauseOnTags.isNotEmpty() && event.tag !in state.pauseOnTags) return false
        val minLevel = state.pauseOnLevelAtLeast ?: return true
        val eventLevel = event.level ?: return false
        return eventLevel.ordinal >= minLevel.ordinal
    }

    private fun releaseCurrentWaiter() {
        currentWaiter?.complete(Unit)
    }
}
