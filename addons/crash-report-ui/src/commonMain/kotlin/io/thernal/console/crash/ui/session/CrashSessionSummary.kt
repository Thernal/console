package io.thernal.console.crash.ui.session

/**
 * List-row view of a past session, built from the directory listing and the tiny sidecars only —
 * the session's log file stays closed until it is opened.
 *
 * @property summary the captured crash summary (exception class + message), or `null` when the
 *   termination left no trace.
 * @property crashedAtMs crash instant from the `.crash` sidecar, or `null` without one.
 */
data class CrashSessionSummary(
    val id: String,
    val startedAtMs: Long,
    val classification: CrashSessionClass,
    val summary: String?,
    val crashedAtMs: Long?,
)
