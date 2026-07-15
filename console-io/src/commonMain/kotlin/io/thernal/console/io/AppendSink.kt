package io.thernal.console.io

/**
 * Append handle for a session's log file. Each [append] hands the bytes to the OS (plain
 * `write`, no user-space buffering and no `fsync`) so the crash-adjacent tail survives process
 * death; only power loss can drop it.
 */
interface AppendSink {
    fun append(bytes: ByteArray): Boolean

    fun close()
}
