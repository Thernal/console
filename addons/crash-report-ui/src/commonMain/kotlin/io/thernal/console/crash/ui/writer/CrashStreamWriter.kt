package io.thernal.console.crash.ui.writer

import io.thernal.console.core.LogObserver
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import io.thernal.console.crash.ui.CrashReports
import io.thernal.console.crash.ui.config.CrashBodyPolicy
import io.thernal.console.crash.ui.config.CrashReportConfig
import io.thernal.console.crash.ui.runtime.CrashReportRuntime
import io.thernal.console.network.NetworkLog

/**
 * Streams every pipeline log into the current session's file as it arrives (inline append,
 * flushed to the OS per record) so the preceding logs survive even *uncatchable* terminations
 * (native crash, OOM kill, force-stop) where no handler ever runs.
 *
 * The save-filter, redactor, and body policy from [CrashReports.config] apply before the append.
 * Runs inside the Console pipeline's single-threaded processing loop; a failed write is isolated
 * by the pipeline's per-observer `runCatching`. Capture follows `Console.isEnabled` — a disabled
 * console never notifies observers, so no separate flag is needed here.
 */
internal object CrashStreamWriter : LogObserver {

    override suspend fun emit(event: Log) {
        val store = CrashReportRuntime.store ?: return
        val config = CrashReports.config.value
        if (!shouldPersist(log = event, config = config)) return
        val redacted = applyRedactor(log = event, config = config) ?: return
        store.appendLog(applyBodyPolicy(log = redacted, config = config))
    }

    /** Mirrors Stepper's `shouldPause`: [CrashReportConfig.persistOnMatch] gates the criteria. */
    private fun shouldPersist(
        log: Log,
        config: CrashReportConfig,
    ): Boolean {
        if (!config.persistOnMatch) return true
        if (config.excludeTags.isNotEmpty() && log.tag in config.excludeTags) return false
        if (config.includeTags.isNotEmpty() && log.tag !in config.includeTags) return false
        val minLevel = config.persistLevelAtLeast ?: return true
        if (log.level == LogLevel.None) return false
        return log.level.ordinal >= minLevel.ordinal
    }

    private fun applyRedactor(
        log: Log,
        config: CrashReportConfig,
    ): Log? {
        val redactor = config.redactor ?: return log
        return redactor(log)
    }

    private fun applyBodyPolicy(
        log: Log,
        config: CrashReportConfig,
    ): Log {
        if (log !is NetworkLog) return log
        val body = log.body ?: return log
        val bounded = when (config.bodyPolicy) {
            CrashBodyPolicy.Full -> return log

            CrashBodyPolicy.None -> null

            CrashBodyPolicy.Truncated -> {
                if (body.length <= config.maxBodySize) return log
                body.take(config.maxBodySize)
            }
        }
        return when (log) {
            is NetworkLog.Request -> log.copy(body = bounded)
            is NetworkLog.Response -> log.copy(body = bounded)
        }
    }
}
