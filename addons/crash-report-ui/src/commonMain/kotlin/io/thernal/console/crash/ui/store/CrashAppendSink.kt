package io.thernal.console.crash.ui.store

/**
 * Append handle for the active session's log file. Each [append] hands the bytes to the OS
 * (plain `write`, no user-space buffering and no `fsync`) so the crash-adjacent tail survives
 * process death; only power loss can drop it.
 */
internal interface CrashAppendSink {
    fun append(bytes: ByteArray): Boolean

    fun close()
}
