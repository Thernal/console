package io.thernal.console.logging.ui.addon

import io.thernal.console.agent.AgentCall
import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.core.LogObserver
import io.thernal.console.core.log.BasicLog
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import io.thernal.console.runtime.console.Console
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ConsoleInternalApi::class)
class LoggingAgentEntriesTest {
    @Test
    fun `logs are a feed and the summary a tool and writing an action`() {
        val entries = loggingAgentEntries()

        assertEquals(listOf("logs"), entries.feeds.map { it.name })
        assertEquals(
            mapOf("logs_summary" to false, "logs_write" to true),
            entries.tools.associate { it.name to it.isAction },
        )
    }

    @Test
    fun `logs_write sends an agent log through the pipeline`(): TestResult {
        return runTest {
            val received = CompletableDeferred<Log>()
            val observer = object : LogObserver {
                override suspend fun emit(event: Log) {
                    if (event.message == MESSAGE) received.complete(event)
                }
            }
            Console.addObserver(observer)
            try {
                val write = loggingAgentEntries().tools.single { it.name == "logs_write" }
                val answer = write.run(AgentCall(mapOf("message" to MESSAGE, "level" to "Warning", "tag" to " ")))

                val log = withContext(Dispatchers.Default) { withTimeout(TIMEOUT_MILLIS) { received.await() } }
                assertTrue(log is BasicLog)
                assertEquals(LogLevel.Warning, log.level)
                assertNull(log.tag)
                assertTrue(answer.contains(log.id))
            } finally {
                Console.removeObserver(observer)
            }
        }
    }

    private companion object {
        const val TIMEOUT_MILLIS = 5_000L
        const val MESSAGE = "step 2"
    }
}
