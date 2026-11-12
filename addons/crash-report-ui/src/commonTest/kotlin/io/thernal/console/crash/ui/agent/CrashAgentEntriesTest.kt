package io.thernal.console.crash.ui.agent

import io.thernal.console.agent.AgentCall
import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.core.log.BasicLog
import io.thernal.console.core.log.LogLevel
import io.thernal.console.crash.ui.handler.CrashCapture
import io.thernal.console.crash.ui.runtime.CrashReportRuntime
import io.thernal.console.io.ConsoleFileSystem
import io.thernal.console.runtime.console.Console
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ConsoleInternalApi::class)
class CrashAgentEntriesTest {
    private lateinit var directoryPath: String
    private val entries = crashAgentEntries()

    @BeforeTest
    fun setUp() {
        Console.isEnabled = true
        CrashCapture.reset()
        CrashReportRuntime.reset()
        directoryPath = "${ConsoleFileSystem.temporaryDirectoryPath()}/crash-agent-test-${Uuid.random()}"
        CrashReportRuntime.startIn(directoryPath)
    }

    @AfterTest
    fun tearDown() {
        CrashCapture.reset()
        CrashReportRuntime.reset()
        ConsoleFileSystem.listFileNames(directoryPath).forEach { ConsoleFileSystem.delete("$directoryPath/$it") }
        ConsoleFileSystem.delete(directoryPath)
    }

    @Test
    fun `a crashed session is listed and opens with its trace and the logs before it`(): TestResult {
        return runTest {
            val id = crash()

            val list = run("crash_sessions", mapOf("crashed_only" to true, "limit" to LIMIT))
            val session = run("crash_session", mapOf("id" to id, "logs" to LOGS, "full_logs" to false))

            assertTrue(list.startsWith("$id · crashed"))
            assertTrue("kaboom" in list)
            assertTrue("Stack trace:" in session)
            assertTrue("kaboom" in session)
            assertTrue("Logs before the end (last 2 of 2):" in session)
            assertTrue(session.trimEnd().endsWith("Warning [Cart] second"))
        }
    }

    @Test
    fun `the crashes feed reports a crash of the previous run`(): TestResult {
        return runTest {
            val id = crash()

            val records = entries.feeds.single { it.name == "crashes" }.records.toList()

            val record = records.single()
            assertEquals(id, record.id)
            assertEquals("Fatal", record.level)
            assertTrue(record.summary.startsWith("The previous run crashed:"))
        }
    }

    @Test
    fun `an unknown session is refused`(): TestResult {
        return runTest {
            val error = assertFailsWith<IllegalArgumentException> {
                run("crash_session", mapOf("id" to "missing", "logs" to LOGS, "full_logs" to false))
            }

            assertEquals("No saved session \"missing\"; crash_sessions lists them", error.message)
        }
    }

    @Test
    fun `crash_delete removes the session`(): TestResult {
        return runTest {
            val id = crash()

            run("crash_delete", mapOf("id" to id))

            assertEquals("No saved sessions.", run("crash_sessions", mapOf("crashed_only" to true, "limit" to LIMIT)))
        }
    }

    private fun crash(): String {
        val store = assertNotNull(CrashReportRuntime.store)
        val id = assertNotNull(store.activeSessionId)
        store.appendLog(BasicLog(message = "first", tag = "Cart", level = LogLevel.Info))
        store.appendLog(BasicLog(message = "second", tag = "Cart", level = LogLevel.Warning))
        CrashCapture.onUncaught(IllegalStateException("kaboom"))
        return id
    }

    private suspend fun run(
        tool: String,
        values: Map<String, Any?>,
    ): String {
        return entries.tools.single { it.name == tool }.run(AgentCall(values))
    }

    private companion object {
        const val LIMIT = 10
        const val LOGS = 30
    }
}
