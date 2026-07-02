@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.crash.ui.writer

import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.core.log.BasicLog
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import io.thernal.console.crash.CrashSessionSerializer
import io.thernal.console.crash.LogCodecRegistry
import io.thernal.console.crash.ui.CrashReports
import io.thernal.console.crash.ui.codec.NetworkRequestLogCodec
import io.thernal.console.crash.ui.config.CrashBodyPolicy
import io.thernal.console.crash.ui.config.CrashReportConfig
import io.thernal.console.crash.ui.runtime.CrashReportRuntime
import io.thernal.console.crash.ui.store.CrashFileSystem
import io.thernal.console.network.NetworkLog
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalUuidApi::class)
class CrashStreamWriterTest {

    private lateinit var directoryPath: String

    @BeforeTest
    fun resetRuntime() {
        CrashReports.updateConfig { CrashReportConfig() }
        CrashReportRuntime.reset()
        directoryPath = "${CrashFileSystem.temporaryDirectoryPath()}/crash-writer-test-${Uuid.random()}"
    }

    @AfterTest
    fun tearDown() {
        CrashReports.updateConfig { CrashReportConfig() }
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

            assertEquals(listOf("streamed"), persistedLogs().map { it.message })
        }
    }

    @Test
    fun `emit without a started runtime drops the log silently`(): TestResult {
        return runTest {
            CrashStreamWriter.emit(BasicLog(message = "dropped"))
        }
    }

    @Test
    fun `save-filter keeps only matching levels when persistOnMatch is on`(): TestResult {
        return runTest {
            CrashReportRuntime.startIn(directoryPath)
            CrashReports.updateConfig {
                copy(persistOnMatch = true, persistLevelAtLeast = LogLevel.Warning)
            }

            CrashStreamWriter.emit(BasicLog(message = "debug", level = LogLevel.Debug))
            CrashStreamWriter.emit(BasicLog(message = "error", level = LogLevel.Error))

            assertEquals(listOf("error"), persistedLogs().map { it.message })
        }
    }

    @Test
    fun `save-filter respects include and exclude tags`(): TestResult {
        return runTest {
            CrashReportRuntime.startIn(directoryPath)
            CrashReports.updateConfig {
                copy(persistOnMatch = true, includeTags = setOf("Keep"), excludeTags = setOf("Drop"))
            }

            CrashStreamWriter.emit(BasicLog(message = "kept", tag = "Keep"))
            CrashStreamWriter.emit(BasicLog(message = "excluded", tag = "Drop"))
            CrashStreamWriter.emit(BasicLog(message = "unlisted", tag = "Other"))

            assertEquals(listOf("kept"), persistedLogs().map { it.message })
        }
    }

    @Test
    fun `redactor can rewrite or drop a log before persistence`(): TestResult {
        return runTest {
            CrashReportRuntime.startIn(directoryPath)
            CrashReports.updateConfig {
                copy(
                    redactor = { log ->
                        when {
                            log.message.contains("secret") -> null
                            log is BasicLog -> log.copy(message = log.message.replace("token", "***"))
                            else -> log
                        }
                    },
                )
            }

            CrashStreamWriter.emit(BasicLog(message = "user secret payload"))
            CrashStreamWriter.emit(BasicLog(message = "auth token refreshed"))

            assertEquals(listOf("auth *** refreshed"), persistedLogs().map { it.message })
        }
    }

    @Test
    fun `body policy None strips network bodies and Truncated caps them`(): TestResult {
        return runTest {
            CrashReportRuntime.startIn(directoryPath)
            registerNetworkCodecsForTest()

            CrashReports.updateConfig { copy(bodyPolicy = CrashBodyPolicy.None) }
            CrashStreamWriter.emit(networkRequest(id = "req-none", body = "sensitive-body"))

            CrashReports.updateConfig { copy(bodyPolicy = CrashBodyPolicy.Truncated, maxBodySize = 4) }
            CrashStreamWriter.emit(networkRequest(id = "req-cut", body = "0123456789"))

            val logs = persistedLogs()
            val stripped = assertIs<NetworkLog.Request>(logs.first { it.id == "req-none" })
            assertNull(stripped.body)
            val truncated = assertIs<NetworkLog.Request>(logs.first { it.id == "req-cut" })
            assertEquals("0123", truncated.body)
        }
    }

    private fun persistedLogs(): List<Log> {
        val store = assertNotNull(CrashReportRuntime.store)
        val sessionId = assertNotNull(store.activeSessionId)
        return store.readLogSegments(sessionId).flatMap { segment ->
            CrashSessionSerializer.deserialize(segment)?.logs.orEmpty()
        }
    }

    private fun networkRequest(
        id: String,
        body: String,
    ): NetworkLog.Request {
        return NetworkLog.Request(
            method = "POST",
            url = "https://api.example.com/data",
            headers = emptyMap(),
            body = body,
            id = id,
        )
    }
}

// Round-tripping NetworkLog.Request in assertions needs the codec; production code registers
// it in CrashReportAddon.onInstall.
private fun registerNetworkCodecsForTest() {
    LogCodecRegistry.register(
        type = NetworkLog.Request::class,
        discriminator = NetworkRequestLogCodec.DISCRIMINATOR,
        codec = NetworkRequestLogCodec,
    )
}
