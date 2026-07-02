package io.thernal.console.crash.ui.handler

import io.thernal.console.core.log.LogLevel
import io.thernal.console.crash.CrashSessionSerializer
import io.thernal.console.crash.ui.runtime.CrashReportRuntime
import io.thernal.console.crash.ui.session.CrashSidecar
import io.thernal.console.crash.ui.store.CrashFileSystem
import io.thernal.console.runtime.console.Console
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CrashCaptureTest {

    private lateinit var directoryPath: String

    @BeforeTest
    fun setUp() {
        CrashCapture.reset()
        CrashReportRuntime.reset()
        Console.isEnabled = true
        directoryPath = "${CrashFileSystem.temporaryDirectoryPath()}/crash-capture-test-${Uuid.random()}"
        CrashReportRuntime.startIn(directoryPath)
    }

    @AfterTest
    fun tearDown() {
        Console.isEnabled = true
        CrashCapture.reset()
        CrashReportRuntime.reset()
        CrashFileSystem.listFileNames(directoryPath).forEach { name ->
            CrashFileSystem.delete("$directoryPath/$name")
        }
        CrashFileSystem.delete(directoryPath)
    }

    @Test
    fun `onUncaught appends the fatal record then writes the sidecar and finalizes the session`() {
        val store = assertNotNull(CrashReportRuntime.store)
        val sessionId = assertNotNull(store.activeSessionId)

        CrashCapture.onUncaught(IllegalStateException("boom"))

        assertNull(store.activeSessionId)

        val entry = store.entries().single()
        assertEquals(sessionId, entry.id)
        assertTrue(entry.hasCrash)

        val sidecar = assertNotNull(CrashSidecar.parse(assertNotNull(store.readCrashSidecar(sessionId))))
        assertTrue(sidecar.summary.contains("boom"))

        val logs = store.readLogSegments(sessionId).flatMap { segment ->
            CrashSessionSerializer.deserialize(segment)?.logs.orEmpty()
        }
        val fatal = logs.last()
        assertEquals(LogLevel.Fatal, fatal.level)
        assertEquals(CrashCapture.CRASH_LOG_TAG, fatal.tag)
        assertTrue(fatal.message.contains("boom"))
    }

    @Test
    fun `the reentrancy guard swallows a second crash`() {
        val store = assertNotNull(CrashReportRuntime.store)
        val sessionId = assertNotNull(store.activeSessionId)

        CrashCapture.onUncaught(IllegalStateException("marker-alpha"))
        CrashCapture.onUncaught(IllegalStateException("marker-beta"))

        val sidecar = assertNotNull(CrashSidecar.parse(assertNotNull(store.readCrashSidecar(sessionId))))
        assertTrue(sidecar.summary.contains("marker-alpha"))
        assertFalse(sidecar.stackTrace.contains("marker-beta"))
    }

    @Test
    fun `a disabled console records nothing but the session stays intact`() {
        val store = assertNotNull(CrashReportRuntime.store)
        val sessionId = assertNotNull(store.activeSessionId)
        Console.isEnabled = false

        CrashCapture.onUncaught(IllegalStateException("hidden"))

        assertEquals(sessionId, store.activeSessionId)
        assertNull(store.readCrashSidecar(sessionId))
    }
}
