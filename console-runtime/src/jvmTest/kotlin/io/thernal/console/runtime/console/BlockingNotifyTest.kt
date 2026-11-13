@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.runtime.console

import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.core.log.BasicLog
import java.awt.EventQueue
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/** A held log (a paused stepper) and `blockingNotify` on the UI thread, which releasing the log needs. */
class BlockingNotifyTest {
    private val observer = HoldingObserver(heldMessage = HELD, timeoutSeconds = TIMEOUT_SECONDS)

    @BeforeTest
    fun install() {
        System.setProperty("java.awt.headless", "true")
        Console.addObserver(observer)
        Console.notify { BasicLog(message = HELD) }
        observer.awaitHeld()
    }

    @AfterTest
    fun remove() {
        observer.release()
        Console.removeObserver(observer)
    }

    @Test
    fun `on the UI thread it queues behind a held log instead of waiting for it`() {
        val returned = CompletableFuture<Unit>()

        EventQueue.invokeLater {
            Console.blockingNotify { BasicLog(message = AFTER) }
            returned.complete(Unit)
        }

        returned.get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        assertEquals(listOf(HELD), observer.messages())
        observer.release()
        observer.awaitCount(count = 2)
        assertEquals(listOf(HELD, AFTER), observer.messages())
    }

    @Test
    fun `off the UI thread it still waits for a held log`() {
        val returned = CompletableFuture.runAsync { Console.blockingNotify { BasicLog(message = AFTER) } }

        assertFalse(runCatching { returned.get(WAIT_MILLIS, TimeUnit.MILLISECONDS) }.isSuccess)
        observer.release()
        returned.get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        assertEquals(listOf(HELD, AFTER), observer.messages())
    }

    private companion object {
        const val HELD = "held"
        const val AFTER = "after"
        const val TIMEOUT_SECONDS = 5L
        const val WAIT_MILLIS = 300L
    }
}
