package io.thernal.console.crash

import io.thernal.console.core.log.BasicLog
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import kotlin.time.Instant

/**
 * Serialization-facing view of a [Log]: the common [Log] fields plus a [type] discriminator and an
 * optional codec-owned [payload].
 *
 * The common fields alone are exactly enough to rebuild a [BasicLog] via [toBasicLog], which is what
 * makes the fallback free — deserialization never *needs* a codec. A [LogCodec] only adds/restores
 * the extra, type-specific fields through [payload].
 */
data class LogEnvelope(
    val type: String,
    val id: String,
    val timestamp: Long,
    val level: String,
    val tag: String?,
    val message: String,
    val groupId: String?,
    val payload: Map<String, String> = emptyMap(),
)

/** Extracts the common [Log] fields into an envelope tagged with [type] and [payload]. */
fun Log.toEnvelope(
    type: String,
    payload: Map<String, String> = emptyMap(),
): LogEnvelope {
    return LogEnvelope(
        type = type,
        id = id,
        timestamp = timestamp.toEpochMilliseconds(),
        level = level.name,
        tag = tag,
        message = message,
        groupId = groupId,
        payload = payload,
    )
}

/** Rebuilds a [BasicLog] from the envelope's common fields, preserving identity and crash-time clock. */
fun LogEnvelope.toBasicLog(): BasicLog {
    return BasicLog(
        message = message,
        tag = tag,
        level = levelOrNone(level),
        id = id,
        timestamp = Instant.fromEpochMilliseconds(timestamp),
        groupId = groupId,
    )
}

private fun levelOrNone(name: String): LogLevel {
    return LogLevel.entries.firstOrNull { it.name == name } ?: LogLevel.None
}
