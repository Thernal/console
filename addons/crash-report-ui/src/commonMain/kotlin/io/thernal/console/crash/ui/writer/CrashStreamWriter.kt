package io.thernal.console.crash.ui.writer

import io.thernal.console.core.LogObserver
import io.thernal.console.core.log.Log
import io.thernal.console.crash.ui.runtime.CrashReportRuntime

/**
 * Streams every pipeline log into the current session's file as it arrives (inline append,
 * flushed to the OS per record) so the preceding logs survive even *uncatchable* terminations
 * (native crash, OOM kill, force-stop) where no handler ever runs.
 *
 * Runs inside the Console pipeline's single-threaded processing loop; a failed write is isolated
 * by the pipeline's per-observer `runCatching`. Capture follows `Console.isEnabled` — a disabled
 * console never notifies observers, so no separate flag is needed here.
 */
internal object CrashStreamWriter : LogObserver {

    override suspend fun emit(event: Log) {
        CrashReportRuntime.store?.appendLog(event)
    }
}
