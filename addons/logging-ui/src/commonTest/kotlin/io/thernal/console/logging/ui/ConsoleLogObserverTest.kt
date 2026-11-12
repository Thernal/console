package io.thernal.console.logging.ui

import io.thernal.console.core.log.BasicLog
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConsoleLogObserverTest {
    @AfterTest
    fun restore() {
        ConsoleLogObserver.updateConfig(ConsoleLogObserver.Config())
        ConsoleLogObserver.clear()
    }

    @Test
    fun `emit never suspends when nobody collects the stream`(): TestResult {
        return runTest {
            val replaySize = ConsoleLogObserver.Config().maxLogCount
            val logs = List(replaySize + EXTRA_LOGS) { BasicLog(message = "log $it") }

            val job = launch(start = CoroutineStart.UNDISPATCHED) {
                logs.forEach { ConsoleLogObserver.emit(it) }
            }

            assertTrue(job.isCompleted)
            assertEquals(logs.takeLast(replaySize), ConsoleLogObserver.stream.replayCache)
        }
    }

    @Test
    fun `logs dropped by the enabled switch are not streamed either`(): TestResult {
        return runTest {
            ConsoleLogObserver.updateConfig { copy(enabled = false) }
            val log = BasicLog(message = "dropped")

            ConsoleLogObserver.emit(log)

            assertFalse(log in ConsoleLogObserver.logs.value)
            assertFalse(log in ConsoleLogObserver.stream.replayCache)
        }
    }

    private companion object {
        const val EXTRA_LOGS = 500
    }
}
