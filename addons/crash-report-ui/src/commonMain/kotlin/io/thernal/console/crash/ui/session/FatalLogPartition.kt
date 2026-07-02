package io.thernal.console.crash.ui.session

import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import io.thernal.console.crash.ui.handler.CrashCapture

/**
 * Splits a restored session's logs into the preceding logs and the fatal crash record — the
 * final [LogLevel.Fatal] log tagged [CrashCapture.CRASH_LOG_TAG] that the crash handler appends.
 */
internal fun List<Log>.partitionFatal(): Pair<List<Log>, Log?> {
    val last = lastOrNull() ?: return this to null
    val isFatalRecord = last.tag == CrashCapture.CRASH_LOG_TAG && last.level == LogLevel.Fatal
    return if (isFatalRecord) dropLast(1) to last else this to null
}
