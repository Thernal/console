package io.thernal.console.crash

import io.thernal.console.core.log.Log
import io.thernal.console.crash.wire.ByteReader
import io.thernal.console.crash.wire.ByteWriter

/**
 * Hand-rolled, length-prefixed (de)serialization engine for a [CrashSession].
 *
 * Container layout:
 * ```
 * [int]    CRASH_FORMAT_VERSION
 * [record] session header (id, crashedAt, summary)
 * [record] log envelope 0
 * [record] log envelope 1
 * ...
 * ```
 * A record is a 4-byte big-endian length prefix followed by that many bytes. On read, a missing or
 * mismatched version discards the whole session; a truncated final record is tolerated (reading
 * stops at the first incomplete record); an unknown discriminator falls that log back to `BasicLog`.
 */
object CrashSessionSerializer {

    private const val VERSION_BYTES = 4

    fun serialize(session: CrashSession): ByteArray {
        val writer = ByteWriter()
        writer.writeInt(CRASH_FORMAT_VERSION)
        writer.writeRecord(encodeHeader(session.header))
        for (log in session.logs) {
            writer.writeRecord(encodeEnvelope(encodeLog(log)))
        }
        return writer.toByteArray()
    }

    /** Full parse: header + every log restored via its codec, else a `BasicLog` fallback. */
    fun deserialize(bytes: ByteArray): CrashSession? {
        val reader = openContainer(bytes) ?: return null
        val header = reader.readRecord()?.let(::decodeHeader) ?: return null
        val logs = buildList {
            while (true) {
                val record = reader.readRecord() ?: break
                val log = runCatching { decodeLog(decodeEnvelope(record)) }.getOrNull()
                if (log != null) add(log)
            }
        }
        return CrashSession(header = header, logs = logs)
    }

    /** Cheap listing parse: reads only the version + header record, never the logs. */
    fun parseHeader(bytes: ByteArray): CrashSessionHeader? {
        val reader = openContainer(bytes) ?: return null
        return reader.readRecord()?.let(::decodeHeader)
    }

    internal fun encodeLog(log: Log): LogEnvelope {
        val encoder = LogCodecRegistry.encoderFor(log)
        val payload = encoder?.let { runCatching { it.codec.encode(log) }.getOrNull() }.orEmpty()
        return log.toEnvelope(
            type = encoder?.discriminator ?: BASIC_DISCRIMINATOR,
            payload = payload,
        )
    }

    internal fun decodeLog(envelope: LogEnvelope): Log {
        val codec = LogCodecRegistry.decoderFor(envelope.type)
        val decoded = codec?.let {
            runCatching { it.decode(envelope = envelope, payload = envelope.payload) }.getOrNull()
        }
        return decoded ?: envelope.toBasicLog()
    }

    private fun openContainer(bytes: ByteArray): ByteReader? {
        val reader = ByteReader(bytes)
        if (reader.remaining < VERSION_BYTES) return null
        if (reader.readInt() != CRASH_FORMAT_VERSION) return null
        return reader
    }

    private fun encodeHeader(header: CrashSessionHeader): ByteArray {
        return ByteWriter()
            .writeString(header.id)
            .writeLong(header.crashedAt)
            .writeString(header.summary)
            .toByteArray()
    }

    private fun decodeHeader(record: ByteArray): CrashSessionHeader? {
        return runCatching {
            val reader = ByteReader(record)
            CrashSessionHeader(
                id = reader.readString().orEmpty(),
                crashedAt = reader.readLong(),
                summary = reader.readString().orEmpty(),
            )
        }.getOrNull()
    }

    private fun encodeEnvelope(envelope: LogEnvelope): ByteArray {
        return ByteWriter()
            .writeString(envelope.type)
            .writeString(envelope.id)
            .writeLong(envelope.timestamp)
            .writeString(envelope.level)
            .writeString(envelope.tag)
            .writeString(envelope.message)
            .writeString(envelope.groupId)
            .writeStringMap(envelope.payload)
            .toByteArray()
    }

    private fun decodeEnvelope(record: ByteArray): LogEnvelope {
        val reader = ByteReader(record)
        return LogEnvelope(
            type = reader.readString().orEmpty(),
            id = reader.readString().orEmpty(),
            timestamp = reader.readLong(),
            level = reader.readString().orEmpty(),
            tag = reader.readString(),
            message = reader.readString().orEmpty(),
            groupId = reader.readString(),
            payload = reader.readStringMap(),
        )
    }
}
