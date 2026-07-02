package io.thernal.console.crash.ui

import io.thernal.console.crash.CrashSession
import io.thernal.console.crash.CrashSessionSerializer
import io.thernal.console.crash.ui.config.CrashReportConfig
import io.thernal.console.crash.ui.runtime.CrashReportRuntime
import io.thernal.console.crash.ui.session.CrashSessionSummary
import io.thernal.console.crash.ui.session.CrashSidecar
import io.thernal.console.crash.ui.session.classify
import io.thernal.console.crash.ui.session.partitionFatal
import io.thernal.console.crash.ui.store.CrashStore
import io.thernal.console.crash.ui.store.CrashStoreEntry
import io.thernal.console.ui.common.extensions.toHms
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Public facade over the crash-report addon: past-session access and management plus the runtime
 * [config]. Sessions list newest first; the *active* (still recording) session is never listed
 * and cannot be deleted.
 */
object CrashReports {

    private const val NO_TRACE_SUMMARY = "Abnormal termination (no captured trace)"

    private val _config = MutableStateFlow(CrashReportConfig())
    val config: StateFlow<CrashReportConfig> = _config.asStateFlow()

    private val _sessions = MutableStateFlow<List<CrashSessionSummary>>(emptyList())
    val sessions: StateFlow<List<CrashSessionSummary>> = _sessions.asStateFlow()

    fun updateConfig(block: CrashReportConfig.() -> CrashReportConfig) {
        _config.value = _config.value.block().normalized()
    }

    /** Re-reads the session list from disk (names + tiny sidecars only). */
    fun refreshSessions() {
        val store = CrashReportRuntime.store
        _sessions.value = store?.entries()?.map { entry -> summaryOf(entry = entry, store = store) }.orEmpty()
    }

    /** Lazily deserializes the whole session — every log via its codec, else the BasicLog fallback. */
    fun open(id: String): CrashSession? {
        val store = CrashReportRuntime.store ?: return null
        val segments = store.readLogSegments(id).mapNotNull(CrashSessionSerializer::deserialize)
        val first = segments.firstOrNull() ?: return null
        return CrashSession(
            header = first.header,
            logs = segments.flatMap { segment -> segment.logs },
        )
    }

    /** Deletes the past session [id]; the active session is refused by the store. */
    fun delete(id: String) {
        CrashReportRuntime.store?.delete(id)
        refreshSessions()
    }

    /** Deletes every past session; the active session survives. */
    fun clearAll() {
        CrashReportRuntime.store?.clearAll()
        refreshSessions()
    }

    /** Shareable plain-text export of the session: summary, stack trace, then the preceding logs. */
    fun share(id: String): String? {
        val session = open(id) ?: return null
        val sidecar = CrashReportRuntime.store?.readCrashSidecar(id)?.let(CrashSidecar::parse)
        val (preceding, fatal) = session.logs.partitionFatal()

        return buildString {
            appendLine("Crash session $id")
            appendLine(sidecar?.summary ?: NO_TRACE_SUMMARY)
            val stackTrace = sidecar?.stackTrace ?: fatal?.message
            stackTrace?.let {
                appendLine()
                appendLine(it)
            }
            if (preceding.isNotEmpty()) {
                appendLine()
                appendLine("Preceding logs:")
                append(
                    preceding.joinToString(separator = "\n\n") { log ->
                        "${log.timestamp.toHms()} ${log.toShareText()}"
                    },
                )
            }
        }.trim()
    }

    private fun summaryOf(
        entry: CrashStoreEntry,
        store: CrashStore,
    ): CrashSessionSummary {
        val sidecar = if (entry.hasCrash) {
            store.readCrashSidecar(entry.id)?.let(CrashSidecar::parse)
        } else {
            null
        }
        return CrashSessionSummary(
            id = entry.id,
            startedAtMs = entry.startedAtMs,
            classification = entry.classify(),
            summary = sidecar?.summary,
            crashedAtMs = sidecar?.crashedAtMs,
        )
    }

    private fun CrashReportConfig.normalized(): CrashReportConfig {
        return copy(
            maxBodySize = maxBodySize.coerceAtLeast(0),
            maxSessions = maxSessions.coerceAtLeast(1),
        )
    }
}
