package io.thernal.console.crash.ui.handler

import io.thernal.console.core.log.BasicLog
import io.thernal.console.core.log.LogLevel
import io.thernal.console.crash.ui.runtime.CrashReportRuntime
import io.thernal.console.crash.ui.session.CrashSidecar
import io.thernal.console.runtime.console.Console
import kotlin.concurrent.Volatile
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Crash-time work, kept minimal because the heap may be inconsistent on the dying thread:
 * append the fatal stack trace as the session's final log record, write the `.crash` sidecar
 * atomically, and finalize the session file. The installer chains to the previous handler
 * afterwards, so the process still dies normally.
 */
internal object CrashCapture {

    /** Tag of the fatal [BasicLog] appended as a session's last record; the detail UI keys on it. */
    const val CRASH_LOG_TAG: String = "Crash"

    @Volatile
    private var isHandling = false

    fun onUncaught(throwable: Throwable) {
        // Reentrancy guard: a second throw from within recording (or a handler loop) must not
        // recurse. Never cleared — the process is already dying.
        if (isHandling) return
        isHandling = true
        runCatching { record(throwable) }
    }

    /** Clears the latched reentrancy guard; tests only. */
    fun reset() {
        isHandling = false
    }

    private fun record(throwable: Throwable) {
        // The streaming writer is an observer and inherits Console.isEnabled implicitly; the
        // handler is not, so it checks explicitly (and still chains via the installer).
        if (!Console.isEnabled) return
        val store = CrashReportRuntime.store ?: return

        val crashedAtMs = Clock.System.now().toEpochMilliseconds()
        val sidecar = CrashSidecar.of(throwable = throwable, crashedAtMs = crashedAtMs)
        store.appendLog(
            BasicLog(
                message = sidecar.stackTrace,
                tag = CRASH_LOG_TAG,
                level = LogLevel.Fatal,
                timestamp = Instant.fromEpochMilliseconds(crashedAtMs),
            ),
        )
        store.writeCrashSidecar(sidecar.encode())
        store.closeSession()
    }
}
