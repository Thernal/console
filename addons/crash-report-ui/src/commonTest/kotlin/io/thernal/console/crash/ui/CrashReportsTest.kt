package io.thernal.console.crash.ui

import io.thernal.console.core.log.BasicLog
import io.thernal.console.core.log.LogLevel
import io.thernal.console.crash.ui.config.CrashReportConfig
import io.thernal.console.crash.ui.handler.CrashCapture
import io.thernal.console.crash.ui.runtime.CrashReportRuntime
import io.thernal.console.crash.ui.session.CrashSessionClass
import io.thernal.console.crash.ui.store.CrashFileSystem
import io.thernal.console.runtime.console.Console
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CrashReportsTest {

    private lateinit var directoryPath: String

    @BeforeTest
    fun setUp() {
        Console.isEnabled = true
        CrashCapture.reset()
        CrashReportRuntime.reset()
        directoryPath = "${CrashFileSystem.temporaryDirectoryPath()}/crash-reports-test-${Uuid.random()}"
        CrashReportRuntime.startIn(directoryPath)
    }

    @AfterTest
    fun tearDown() {
        CrashReports.updateConfig { CrashReportConfig() }
        CrashCapture.reset()
        CrashReportRuntime.reset()
        CrashFileSystem.listFileNames(directoryPath).forEach { name ->
            CrashFileSystem.delete("$directoryPath/$name")
        }
        CrashFileSystem.delete(directoryPath)
    }

    @Test
    fun `refreshSessions surfaces a crashed session as Confirmed with its summary`() {
        val store = assertNotNull(CrashReportRuntime.store)
        store.appendLog(BasicLog(message = "before the crash"))
        CrashCapture.onUncaught(IllegalStateException("kaboom"))

        CrashReports.refreshSessions()

        val summary = CrashReports.sessions.value.single()
        assertEquals(CrashSessionClass.Confirmed, summary.classification)
        assertTrue(assertNotNull(summary.summary).contains("kaboom"))
        assertNotNull(summary.crashedAtMs)
    }

    @Test
    fun `open restores the session logs lazily`() {
        val store = assertNotNull(CrashReportRuntime.store)
        val sessionId = assertNotNull(store.activeSessionId)
        store.appendLog(BasicLog(message = "first"))
        store.appendLog(BasicLog(message = "second"))
        store.closeSession()

        val session = assertNotNull(CrashReports.open(sessionId))

        assertEquals(listOf("first", "second"), session.logs.map { it.message })
        assertEquals(sessionId, session.header.id)
    }

    @Test
    fun `delete and clearAll update the sessions flow`() {
        val store = assertNotNull(CrashReportRuntime.store)
        val sessionId = assertNotNull(store.activeSessionId)
        store.closeSession()

        CrashReports.refreshSessions()
        assertEquals(1, CrashReports.sessions.value.size)

        CrashReports.delete(sessionId)
        assertTrue(CrashReports.sessions.value.isEmpty())
    }

    @Test
    fun `share exports the trace and the preceding logs without duplicating the fatal record`() {
        val store = assertNotNull(CrashReportRuntime.store)
        val sessionId = assertNotNull(store.activeSessionId)
        store.appendLog(BasicLog(message = "preceding-log-entry", level = LogLevel.Info))
        CrashCapture.onUncaught(IllegalStateException("shared-crash"))

        val text = assertNotNull(CrashReports.share(sessionId))

        assertTrue(text.contains("shared-crash"))
        assertTrue(text.contains("preceding-log-entry"))
        assertEquals(1, Regex("Preceding logs:").findAll(text).count())
    }

    @Test
    fun `share returns null for an unknown session`() {
        assertNull(CrashReports.share("missing"))
    }

    @Test
    fun `updateConfig normalizes out-of-range values`() {
        CrashReports.updateConfig { copy(maxBodySize = -5, maxSessions = 0) }

        assertEquals(0, CrashReports.config.value.maxBodySize)
        assertEquals(1, CrashReports.config.value.maxSessions)
    }
}
