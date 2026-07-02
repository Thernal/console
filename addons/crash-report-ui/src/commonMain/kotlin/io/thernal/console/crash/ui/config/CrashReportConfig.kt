package io.thernal.console.crash.ui.config

import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel

/**
 * Runtime configuration for crash-report persistence, StateFlow-backed via
 * `CrashReports.updateConfig { … }` (mirrors the `logging-ui` / `Stepper` config style).
 *
 * There is no separate `enabled` flag: the streaming writer is a Console observer and follows
 * `Console.isEnabled`; the crash handler checks the same flag explicitly.
 *
 * The save-filter (mirroring Stepper's pause filter) decides *which* logs are persisted. It is
 * coarse — it keeps or drops a whole log. Field-level masking happens upstream at capture
 * (e.g. `SensitiveHeaders` in the network interceptors) or via [redactor].
 *
 * @property showSafeSessions when `true` the session list also shows Safe terminations
 *   (background/clean kills); hidden by default.
 * @property persistOnMatch when `false` (default) every log is persisted; when `true` the
 *   level/tag criteria below must match.
 * @property persistLevelAtLeast minimum [LogLevel] to persist ([LogLevel.None] never matches).
 * @property includeTags when non-empty, only these tags are persisted.
 * @property excludeTags tags never persisted.
 * @property bodyPolicy how much of a network log body reaches disk.
 * @property maxBodySize character cap applied by [CrashBodyPolicy.Truncated].
 * @property maxSessions on-disk session retention cap; evicted oldest-first when the *next*
 *   session starts, so a change applies from the next launch.
 * @property redactor optional last-chance hook: return a scrubbed copy to persist, or `null`
 *   to drop the log entirely.
 */
data class CrashReportConfig(
    val showSafeSessions: Boolean = false,
    val persistOnMatch: Boolean = false,
    val persistLevelAtLeast: LogLevel? = null,
    val includeTags: Set<String> = emptySet(),
    val excludeTags: Set<String> = emptySet(),
    val bodyPolicy: CrashBodyPolicy = CrashBodyPolicy.Full,
    val maxBodySize: Int = DEFAULT_MAX_BODY_SIZE,
    val maxSessions: Int = DEFAULT_MAX_SESSIONS,
    val redactor: ((Log) -> Log?)? = null,
) {

    companion object {
        const val DEFAULT_MAX_BODY_SIZE: Int = 4_096
        const val DEFAULT_MAX_SESSIONS: Int = 10
    }
}
