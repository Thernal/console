package io.thernal.console.crash

import io.thernal.console.core.log.Log

/**
 * Restores full fidelity for a custom [Log] type across a crash-session round-trip.
 *
 * A codec only speaks [Map] — it stays free of any serialization framework. [encode] returns the
 * extra fields a type has *beyond* the common envelope; [decode] rebuilds the full log from that
 * envelope plus the extra [payload]. Returning `null` from [decode] falls the log back to a
 * [io.thernal.console.core.log.BasicLog].
 */
interface LogCodec<T : Log> {
    /** Extra, type-specific fields beyond the common envelope. */
    fun encode(log: T): Map<String, String>

    /** Rebuilds the full log from the common [envelope] plus the extra [payload]; `null` → fallback. */
    fun decode(
        envelope: LogEnvelope,
        payload: Map<String, String>,
    ): T?
}
