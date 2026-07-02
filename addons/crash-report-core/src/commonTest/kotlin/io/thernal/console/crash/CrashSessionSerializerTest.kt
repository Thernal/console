package io.thernal.console.crash

import io.thernal.console.core.log.BasicLog
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Instant

class CrashSessionSerializerTest {

    @Test
    fun `round-trips a basic log with all common fields preserved`() {
        val session = sessionOf(
            BasicLog(
                message = "hello",
                tag = "Auth",
                level = LogLevel.Error,
                id = "log-1",
                timestamp = Instant.fromEpochMilliseconds(1_700_000_000_000),
                groupId = "group-1",
            ),
        )

        val restored = CrashSessionSerializer.deserialize(CrashSessionSerializer.serialize(session))

        assertEquals(session.header, restored?.header)
        val log = assertIs<BasicLog>(restored?.logs?.single())
        assertEquals("hello", log.message)
        assertEquals("Auth", log.tag)
        assertEquals(LogLevel.Error, log.level)
        assertEquals("log-1", log.id)
        assertEquals(1_700_000_000_000, log.timestamp.toEpochMilliseconds())
        assertEquals("group-1", log.groupId)
    }

    @Test
    fun `preserves log order`() {
        val session = sessionOf(
            BasicLog(message = "first", id = "a"),
            BasicLog(message = "second", id = "b"),
            BasicLog(message = "third", id = "c"),
        )

        val restored = CrashSessionSerializer.deserialize(CrashSessionSerializer.serialize(session))

        assertEquals(listOf("a", "b", "c"), restored?.logs?.map { it.id })
    }

    @Test
    fun `tolerates a truncated final record`() {
        val session = sessionOf(
            BasicLog(message = "kept", id = "a"),
            BasicLog(message = "torn", id = "b"),
        )
        val bytes = CrashSessionSerializer.serialize(session)

        val restored = CrashSessionSerializer.deserialize(bytes.copyOf(bytes.size - 2))

        assertEquals(session.header, restored?.header)
        assertEquals(listOf("a"), restored?.logs?.map { it.id })
    }

    @Test
    fun `discards a session with a mismatched version`() {
        val bytes = CrashSessionSerializer.serialize(sessionOf(BasicLog(message = "x")))
        val tampered = bytes.copyOf()
        tampered[VERSION_LOW_BYTE] = (tampered[VERSION_LOW_BYTE] + 1).toByte()

        assertNull(CrashSessionSerializer.deserialize(tampered))
        assertNull(CrashSessionSerializer.parseHeader(tampered))
    }

    @Test
    fun `discards bytes too short to hold a version`() {
        assertNull(CrashSessionSerializer.deserialize(ByteArray(0)))
        assertNull(CrashSessionSerializer.deserialize(ByteArray(VERSION_LOW_BYTE)))
    }

    @Test
    fun `parses the header without deserializing logs`() {
        val session = sessionOf(BasicLog(message = "x"), BasicLog(message = "y"))

        val header = CrashSessionSerializer.parseHeader(CrashSessionSerializer.serialize(session))

        assertEquals(session.header, header)
    }

    @Test
    fun `falls back to a basic log for an unknown discriminator`() {
        val envelope = LogEnvelope(
            type = "totally.unknown",
            id = "log-1",
            timestamp = 42,
            level = LogLevel.Warning.name,
            tag = "T",
            message = "orphan",
            groupId = null,
        )

        val log = CrashSessionSerializer.decodeLog(envelope)

        val basic = assertIs<BasicLog>(log)
        assertEquals("orphan", basic.message)
        assertEquals(LogLevel.Warning, basic.level)
    }

    @Test
    fun `encodes an unregistered log as the basic discriminator with an empty payload`() {
        val envelope = CrashSessionSerializer.encodeLog(BasicLog(message = "x"))

        assertEquals(BASIC_DISCRIMINATOR, envelope.type)
        assertEquals(emptyMap(), envelope.payload)
    }

    private fun sessionOf(vararg logs: Log): CrashSession {
        return CrashSession(
            header = CrashSessionHeader(id = "session-1", crashedAt = 999, summary = "Boom: line 1"),
            logs = logs.toList(),
        )
    }

    private companion object {
        const val VERSION_LOW_BYTE = 3
    }
}
