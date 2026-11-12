package io.thernal.console.crash.ui.agent

import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import io.thernal.console.crash.ui.CrashReports
import io.thernal.console.crash.ui.runtime.CrashReportRuntime
import io.thernal.console.crash.ui.session.CrashSessionClass
import io.thernal.console.crash.ui.session.CrashSessionSummary
import io.thernal.console.crash.ui.session.CrashSidecar
import io.thernal.console.crash.ui.session.partitionFatal
import io.thernal.console.ui.common.extensions.toHms
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

private const val MAX_TRACE_LINES = 80
private const val MAX_TEXT_CHARS = 60_000
private const val MAX_LINE_CHARS = 200

/** The `crash_sessions` answer: one line per saved session, newest first. */
internal fun crashSessionsText(
    crashedOnly: Boolean,
    limit: Int,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): String {
    CrashReports.refreshSessions()
    val all = CrashReports.sessions.value
    val shown = all.filter { !crashedOnly || it.classification != CrashSessionClass.Safe }
    if (shown.isEmpty()) {
        return if (all.isEmpty()) {
            "No saved sessions."
        } else {
            "No crashed sessions; ${all.size} saved sessions ended without one (crashed_only = false lists them)."
        }
    }
    return shown.take(limit).joinToString(separator = "\n") { sessionLine(it, timeZone) }
}

/** The `crash_session` answer: the outcome, the stack trace and the last [logCount] logs before the end. */
internal fun crashSessionText(
    id: String,
    logCount: Int,
    fullLogs: Boolean,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): String {
    CrashReports.refreshSessions()
    val summary = CrashReports.sessions.value.firstOrNull { it.id == id }
    val session = CrashReports.open(id)
    require(summary != null && session != null) { "No saved session \"$id\"; crash_sessions lists them" }
    val sidecar = CrashReportRuntime.store?.readCrashSidecar(id)?.let(CrashSidecar::parse)
    val (preceding, fatal) = session.logs.partitionFatal()
    val text = buildString {
        appendLine(sessionLine(summary, timeZone))
        val trace = sidecar?.stackTrace ?: fatal?.message
        if (trace == null) {
            appendLine("No stack trace: the app ended without a captured exception.")
        } else {
            appendLine()
            appendLine("Stack trace:")
            appendLine(cutLines(trace, MAX_TRACE_LINES))
        }
        val shown = preceding.takeLast(logCount)
        appendLine()
        appendLine("Logs before the end (last ${shown.size} of ${preceding.size}):")
        shown.forEach { log -> appendLine(logText(log, fullLogs, timeZone)) }
    }.trim()
    return cutText(text)
}

internal fun sessionLine(
    summary: CrashSessionSummary,
    timeZone: TimeZone,
): String {
    val outcome = when (summary.classification) {
        CrashSessionClass.Confirmed -> "crashed"
        CrashSessionClass.Probable -> "probably crashed (no trace: native crash, OOM kill or ANR)"
        CrashSessionClass.Safe -> "ended (likely an OS kill or a normal exit)"
    }
    val crashed = summary.crashedAtMs?.let { " · crashed ${dateTime(it, timeZone)}" }.orEmpty()
    val what = summary.summary?.let { " · $it" }.orEmpty()
    return "${summary.id} · $outcome · started ${dateTime(summary.startedAtMs, timeZone)}$crashed$what"
}

/** The level of the record a crash leaves in the `crashes` feed. */
internal fun crashLevel(classification: CrashSessionClass): LogLevel {
    return if (classification == CrashSessionClass.Confirmed) LogLevel.Fatal else LogLevel.Error
}

private fun logText(
    log: Log,
    full: Boolean,
    timeZone: TimeZone,
): String {
    val time = log.timestamp.toHms(timeZone)
    if (full) return "$time ${log.toShareText()}"
    val level = if (log.level == LogLevel.None) "" else " ${log.level.name}"
    val tag = log.tag?.let { " [$it]" }.orEmpty()
    val line = log.message.lineSequence().firstOrNull().orEmpty().take(MAX_LINE_CHARS)
    return "$time$level$tag $line"
}

private fun dateTime(
    epochMillis: Long,
    timeZone: TimeZone,
): String {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    return "${instant.toLocalDateTime(timeZone).date} ${instant.toHms(timeZone)}"
}

private fun cutLines(
    text: String,
    maxLines: Int,
): String {
    val lines = text.lines()
    if (lines.size <= maxLines) return text
    return lines.take(maxLines).joinToString(separator = "\n") + "\n… ${lines.size - maxLines} more lines"
}

private fun cutText(text: String): String {
    if (text.length <= MAX_TEXT_CHARS) return text
    return text.take(MAX_TEXT_CHARS) + "\n… cut: ${text.length - MAX_TEXT_CHARS} more characters; pass fewer `logs`"
}
