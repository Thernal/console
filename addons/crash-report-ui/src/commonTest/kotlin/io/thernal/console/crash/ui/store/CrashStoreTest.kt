package io.thernal.console.crash.ui.store

import io.thernal.console.core.log.BasicLog
import io.thernal.console.core.log.Log
import io.thernal.console.crash.CrashSessionSerializer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CrashStoreTest {

    private lateinit var directoryPath: String

    @BeforeTest
    fun createDirectory() {
        directoryPath = "${CrashFileSystem.temporaryDirectoryPath()}/crash-store-test-${Uuid.random()}"
    }

    @AfterTest
    fun wipeDirectory() {
        CrashFileSystem.listFileNames(directoryPath).forEach { name ->
            CrashFileSystem.delete("$directoryPath/$name")
        }
        CrashFileSystem.delete(directoryPath)
    }

    @Test
    fun `streamed logs survive a reopen without a clean close`() {
        val store = CrashStore(directoryPath)
        assertTrue(store.startSession(id = "session-a", startedAtMs = 1_000))
        store.appendLog(BasicLog(message = "one"))
        store.appendLog(BasicLog(message = "two"))
        store.appendLog(BasicLog(message = "three"))

        // A fresh instance over the same directory — the previous one is never closed,
        // mirroring a killed process.
        val reopened = CrashStore(directoryPath)
        val logs = readLogs(store = reopened, id = "session-a")

        assertEquals(listOf("one", "two", "three"), logs.map { it.message })
    }

    @Test
    fun `segment rotation keeps the newest logs and drops the oldest`() {
        val store = CrashStore(directoryPath, maxRecordsPerSegment = 3)
        assertTrue(store.startSession(id = "session-a", startedAtMs = 1_000))
        for (index in 1..8) {
            store.appendLog(BasicLog(message = "log-$index"))
        }
        store.closeSession()

        val logs = readLogs(store = store, id = "session-a")

        assertEquals(listOf("log-4", "log-5", "log-6", "log-7", "log-8"), logs.map { it.message })
    }

    @Test
    fun `entries lists past sessions newest first and excludes the active one`() {
        val store = CrashStore(directoryPath)
        store.startSession(id = "a", startedAtMs = 1_000)
        store.startSession(id = "b", startedAtMs = 2_000)
        store.startSession(id = "c", startedAtMs = 3_000)

        assertEquals(listOf("b", "a"), store.entries().map { it.id })
    }

    @Test
    fun `entries carries sidecar presence and state`() {
        val store = CrashStore(directoryPath)
        store.startSession(id = "a", startedAtMs = 1_000)
        store.writeCrashSidecar("Boom: stack trace")
        store.writeStateSidecar("FOREGROUND")
        store.startSession(id = "b", startedAtMs = 2_000)

        val entry = store.entries().single()

        assertEquals("a", entry.id)
        assertTrue(entry.hasCrash)
        assertEquals("FOREGROUND", entry.state)
        assertEquals("Boom: stack trace", store.readCrashSidecar("a"))
    }

    @Test
    fun `retention evicts the oldest sessions beyond the cap`() {
        val store = CrashStore(directoryPath, maxSessions = 3)
        for (index in 1..5) {
            store.startSession(id = "s$index", startedAtMs = index * 1_000L)
            store.appendLog(BasicLog(message = "in s$index"))
        }

        assertEquals(listOf("s4", "s3"), store.entries().map { it.id })
        assertTrue(readLogs(store = store, id = "s1").isEmpty())
        assertTrue(readLogs(store = store, id = "s2").isEmpty())
    }

    @Test
    fun `delete removes a past session but refuses the active one`() {
        val store = CrashStore(directoryPath)
        store.startSession(id = "a", startedAtMs = 1_000)
        store.startSession(id = "b", startedAtMs = 2_000)

        assertTrue(store.delete("a"))
        assertFalse(store.delete("b"))
        assertTrue(store.entries().isEmpty())
        assertTrue(readLogs(store = store, id = "a").isEmpty())
    }

    @Test
    fun `clearAll wipes past sessions and keeps the active one`() {
        val store = CrashStore(directoryPath)
        store.startSession(id = "a", startedAtMs = 1_000)
        store.startSession(id = "b", startedAtMs = 2_000)
        store.startSession(id = "c", startedAtMs = 3_000)
        store.appendLog(BasicLog(message = "active"))

        store.clearAll()

        assertTrue(store.entries().isEmpty())
        assertEquals(listOf("active"), readLogs(store = store, id = "c").map { it.message })
    }

    @Test
    fun `state sidecar overwrites atomically`() {
        val store = CrashStore(directoryPath)
        store.startSession(id = "a", startedAtMs = 1_000)
        store.writeStateSidecar("FOREGROUND")
        store.writeStateSidecar("BACKGROUND")

        assertEquals("BACKGROUND", store.readStateSidecar("a"))
    }

    @Test
    fun `append without a session is a no-op`() {
        val store = CrashStore(directoryPath)

        assertFalse(store.appendLog(BasicLog(message = "dropped")))
        assertTrue(store.entries().isEmpty())
    }

    private fun readLogs(
        store: CrashStore,
        id: String,
    ): List<Log> {
        return store.readLogSegments(id).flatMap { segment ->
            CrashSessionSerializer.deserialize(segment)?.logs.orEmpty()
        }
    }
}
