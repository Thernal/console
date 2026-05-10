package az.theternal.console.debugstepper

import az.theternal.console.core.base.Log
import az.theternal.console.core.base.LogObserver
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.concurrent.Volatile

object DebugStepper : LogObserver {

    private const val MAX_RETAINED_EVENTS = 20

    private val _state = MutableStateFlow(DebugStepperState())
    val state: StateFlow<DebugStepperState> = _state.asStateFlow()

    // @Volatile: written by the coroutine dispatcher, read by UI-thread callers
    // (next / setPaused / setEnabled). Without it the JVM gives no visibility guarantee.
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

    fun next() {
        releaseCurrentWaiter()
    }

    // ── LogObserver ───────────────────────────────────────────────────────────

    override suspend fun emit(event: Log) {
        _state.update { current ->
            current.copy(
                events = (current.events + event).takeLast(MAX_RETAINED_EVENTS),
                pendingLogs = current.pendingLogs + 1,
                // blockedTag is NOT set here — only set in awaitIfNeeded when
                // actually blocking, so it never shows stale info for pass-through events.
            )
        }
        try {
            awaitIfNeeded(event)
        } finally {
            _state.update { it.copy(pendingLogs = (it.pendingLogs - 1).coerceAtLeast(0)) }
        }
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private suspend fun awaitIfNeeded(event: Log) {
        if (!_state.value.enabled || !_state.value.paused) return

        val waiter = CompletableDeferred<Unit>()

        // Register the waiter BEFORE the second check.
        //
        // TOCTOU fix: if we checked paused, then setPaused(false) fired (finding
        // currentWaiter=null and completing nothing), then we set currentWaiter and
        // suspended — we'd deadlock forever.
        //
        // By setting currentWaiter first, any releaseCurrentWaiter() call that
        // races with us will always find the waiter and complete it, so the
        // subsequent waiter.await() returns immediately rather than blocking forever.
        currentWaiter = waiter

        // Re-check after registering. Two races are closed here:
        // 1. setPaused(false)/setEnabled(false) fired between first check and
        //    currentWaiter=waiter — state is now "don't block", bail out.
        // 2. next() fired in that same window and already completed the waiter —
        //    waiter.isCompleted guards against proceeding into a no-op await that
        //    would still briefly emit a false "blocked" state update.
        if (!_state.value.enabled || !_state.value.paused || waiter.isCompleted) {
            currentWaiter = null
            return
        }

        _state.update {
            it.copy(
                blockedLogId = event.id,
                blockedTag = event.tag,
            )
        }
        try {
            waiter.await()
        } finally {
            currentWaiter = null
            _state.update { it.copy(blockedLogId = null, blockedTag = null) }
        }
    }

    private fun releaseCurrentWaiter() {
        // Only signal — do NOT null out here. The coroutine's finally block owns
        // the cleanup. Nulling here races with the next event writing its own
        // waiter: releaseCurrentWaiter fires, nulls the field, and the next
        // event's waiter2 gets overwritten — making all subsequent next()/resume
        // calls permanent no-ops while the coroutine is stuck at waiter.await().
        currentWaiter?.complete(Unit)
    }
}
