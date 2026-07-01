package io.thernal.console.crash

/**
 * Self-contained head of a crash session, laid out first in the container so the session list can be
 * built by reading headers alone — without deserializing every log.
 *
 * @property crashedAt epoch milliseconds.
 * @property summary exception class + first stack-trace line.
 */
data class CrashSessionHeader(
    val id: String,
    val crashedAt: Long,
    val summary: String,
)
