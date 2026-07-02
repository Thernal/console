package io.thernal.console.crash.ui.session

import io.thernal.console.core.log.BasicLog
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import kotlin.time.Instant

/**
 * Maps a session summary onto a [Log] so the session list renders through the shared log-item UI
 * (`LocalLogRenderer`): the classification becomes the tag chip and its severity drives the
 * accent color via the standard level mapping.
 */
internal fun CrashSessionSummary.toDisplayLog(): Log {
    return BasicLog(
        message = summary ?: classification.fallbackSummary(),
        tag = classification.name,
        level = classification.displayLevel(),
        id = id,
        timestamp = Instant.fromEpochMilliseconds(crashedAtMs ?: startedAtMs),
    )
}

private fun CrashSessionClass.displayLevel(): LogLevel {
    return when (this) {
        CrashSessionClass.Confirmed -> LogLevel.Fatal
        CrashSessionClass.Probable -> LogLevel.Warning
        CrashSessionClass.Safe -> LogLevel.Verbose
    }
}

private fun CrashSessionClass.fallbackSummary(): String {
    return when (this) {
        CrashSessionClass.Confirmed -> "Captured crash"
        CrashSessionClass.Probable -> "Abnormal termination (no captured trace)"
        CrashSessionClass.Safe -> "Killed in background"
    }
}
