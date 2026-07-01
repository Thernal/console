package io.thernal.console.crash

import io.thernal.console.core.log.Log

/**
 * One persisted crash: its [header] plus the ordered preceding [logs], with the fatal crash log last.
 */
data class CrashSession(
    val header: CrashSessionHeader,
    val logs: List<Log>,
)
