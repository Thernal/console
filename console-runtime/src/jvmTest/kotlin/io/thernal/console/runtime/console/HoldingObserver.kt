package io.thernal.console.runtime.console

import io.thernal.console.core.LogObserver
import io.thernal.console.core.log.Log
import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** An observer that parks the log with [heldMessage] in `emit` until [release], the way a paused stepper does. */
internal class HoldingObserver(
    private val heldMessage: String,
    private val timeoutSeconds: Long,
) : LogObserver {
    private val received = CopyOnWriteArrayList<String>()
    private val held = CountDownLatch(1)
    private val gate = CompletableDeferred<Unit>()

    override suspend fun emit(event: Log) {
        received += event.message
        if (event.message == heldMessage) {
            held.countDown()
            gate.await()
        }
    }

    fun awaitHeld() {
        check(held.await(timeoutSeconds, TimeUnit.SECONDS)) { "The log was never held" }
    }

    fun awaitCount(count: Int) {
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(timeoutSeconds)
        while (received.size < count) {
            check(System.nanoTime() < deadline) { "Only ${received.size} of $count logs arrived" }
            Thread.sleep(POLL_MILLIS)
        }
    }

    fun release() {
        gate.complete(Unit)
    }

    fun messages(): List<String> {
        return received.toList()
    }

    private companion object {
        const val POLL_MILLIS = 10L
    }
}
