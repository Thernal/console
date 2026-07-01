package io.thernal.console.crash.ui.writer

import io.thernal.console.core.log.BasicLog
import io.thernal.console.crash.CrashSessionSerializer
import io.thernal.console.crash.ui.runtime.CrashReportRuntime
import io.thernal.console.crash.ui.store.CrashFileSystem
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalUuidApi::class)
class CrashStreamWriterTest {

    private lateinit var directoryPath: String

    @BeforeTest
    fun resetRuntime() {
        CrashReportRuntime.reset()
        directoryPath = "${CrashFileSystem.temporaryDirectoryPath()}/crash-writer-test-${Uuid.random()}"
    }

    @AfterTest
    fun tearDown() {
        CrashReportRuntime.reset()
        CrashFileSystem.listFileNames(directoryPath).forEach { name ->
            CrashFileSystem.delete("$directoryPath/$name")
        }
        CrashFileSystem.delete(directoryPath)
    }

    @Test
    fun `emit streams the log into the active session file`(): TestResult {
        return runTest {
            CrashReportRuntime.startIn(directoryPath)

            CrashStreamWriter.emit(BasicLog(message = "streamed"))

            val store = assertNotNull(CrashReportRuntime.store)
            val sessionId = assertNotNull(store.activeSessionId)
            val logs = store.readLogSegments(sessionId).flatMap { segment ->
                CrashSessionSerializer.deserialize(segment)?.logs.orEmpty()
            }
            assertEquals(listOf("streamed"), logs.map { it.message })
        }
    }

    @Test
    fun `emit without a started runtime drops the log silently`(): TestResult {
        return runTest {
            CrashStreamWriter.emit(BasicLog(message = "dropped"))
        }
    }
}
