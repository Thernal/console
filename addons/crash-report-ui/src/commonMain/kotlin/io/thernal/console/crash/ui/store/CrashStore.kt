package io.thernal.console.crash.ui.store

import io.thernal.console.core.log.Log
import io.thernal.console.crash.CrashSessionHeader
import io.thernal.console.crash.CrashSessionSerializer
import kotlin.concurrent.Volatile

/**
 * Per-session file persistence for crash reports.
 *
 * Layout inside [directoryPath] (sidecar naming per the crash-report research):
 * - `<startedAtEpochMs>__<id>.log` — streamed log records (current segment).
 * - `<stem>.log.old` — previous segment, kept so a bounded window of the *newest* logs survives
 *   (a plain record cap would drop the crash-adjacent tail, the most valuable part).
 * - `<stem>.crash` — crash summary + stack trace, written only on a captured crash.
 * - `<stem>.state` — last foreground/background lifecycle state.
 *
 * Appends go straight to the OS per record; sidecars are written atomically (temp + rename).
 * Each segment starts with [CrashSessionSerializer.encodeStreamPrefix], so a segment file is a
 * regular session container and a torn tail is tolerated on read.
 */
internal class CrashStore(
    private val directoryPath: String,
    private val maxSessions: Int = DEFAULT_MAX_SESSIONS,
    private val maxRecordsPerSegment: Int = DEFAULT_MAX_RECORDS_PER_SEGMENT,
) {

    @Volatile
    private var activeStem: String? = null

    @Volatile
    private var activeSink: CrashAppendSink? = null

    @Volatile
    private var activeHeader: CrashSessionHeader? = null

    @Volatile
    private var recordsInSegment: Int = 0

    val activeSessionId: String? get() = activeHeader?.id

    /**
     * Closes any previous session, evicts the oldest past sessions beyond [maxSessions] - 1, and
     * opens a fresh `.log` for streaming. Returns `false` when the directory or file cannot be
     * created — the store then silently drops appends.
     */
    fun startSession(
        id: String,
        startedAtMs: Long,
    ): Boolean {
        if (!CrashFileSystem.ensureDirectory(directoryPath)) return false
        closeSession()
        applyRetention()

        val stem = "$startedAtMs$STEM_SEPARATOR$id"
        val header = CrashSessionHeader(id = id, crashedAt = startedAtMs, summary = "")
        val sink = CrashFileSystem.openAppend(filePath(stem = stem, suffix = LOG_SUFFIX)) ?: return false
        if (!sink.append(CrashSessionSerializer.encodeStreamPrefix(header))) {
            sink.close()
            return false
        }

        activeStem = stem
        activeHeader = header
        activeSink = sink
        recordsInSegment = 0
        return true
    }

    /** Encodes and appends one log record to the active session; no-op without a session. */
    fun appendLog(log: Log): Boolean {
        if (activeSink == null) return false
        if (recordsInSegment >= maxRecordsPerSegment) rotateSegment()
        val sink = activeSink ?: return false
        val isWritten = sink.append(CrashSessionSerializer.encodeRecord(log))
        if (isWritten) recordsInSegment += 1
        return isWritten
    }

    /** Atomically writes the `.crash` sidecar (summary + stack trace) for the active session. */
    fun writeCrashSidecar(content: String): Boolean {
        val stem = activeStem ?: return false
        return CrashFileSystem.writeAtomically(
            path = filePath(stem = stem, suffix = CRASH_SUFFIX),
            bytes = content.encodeToByteArray(),
        )
    }

    /** Atomically overwrites the `.state` sidecar (last fg/bg state) for the active session. */
    fun writeStateSidecar(state: String): Boolean {
        val stem = activeStem ?: return false
        return CrashFileSystem.writeAtomically(
            path = filePath(stem = stem, suffix = STATE_SUFFIX),
            bytes = state.encodeToByteArray(),
        )
    }

    /** Closes the active session's sink; its files remain on disk as a past session. */
    fun closeSession() {
        activeSink?.close()
        activeSink = null
        activeStem = null
        activeHeader = null
        recordsInSegment = 0
    }

    /** Past sessions (the active one excluded), newest first, built from names + sidecars only. */
    fun entries(): List<CrashStoreEntry> {
        return pastStems().map { parsed ->
            CrashStoreEntry(
                id = parsed.id,
                startedAtMs = parsed.startedAtMs,
                hasCrash = CrashFileSystem.exists(filePath(stem = parsed.stem, suffix = CRASH_SUFFIX)),
                state = readSidecar(stem = parsed.stem, suffix = STATE_SUFFIX),
            )
        }
    }

    /** Raw log segments for [id], oldest first; deserialize each via `CrashSessionSerializer`. */
    fun readLogSegments(id: String): List<ByteArray> {
        val stem = findStem(id) ?: return emptyList()
        return listOfNotNull(
            CrashFileSystem.readBytes(filePath(stem = stem, suffix = OLD_LOG_SUFFIX)),
            CrashFileSystem.readBytes(filePath(stem = stem, suffix = LOG_SUFFIX)),
        )
    }

    fun readCrashSidecar(id: String): String? {
        val stem = findStem(id) ?: return null
        return readSidecar(stem = stem, suffix = CRASH_SUFFIX)
    }

    fun readStateSidecar(id: String): String? {
        val stem = findStem(id) ?: return null
        return readSidecar(stem = stem, suffix = STATE_SUFFIX)
    }

    /** Deletes all files of the past session [id]; the active session is refused. */
    fun delete(id: String): Boolean {
        if (id == activeSessionId) return false
        val stem = findStem(id) ?: return false
        deleteSessionFiles(stem)
        return true
    }

    /** Deletes every past session; the active session survives. */
    fun clearAll() {
        pastStems().forEach { parsed -> deleteSessionFiles(parsed.stem) }
    }

    private fun rotateSegment() {
        val stem = activeStem ?: return
        val header = activeHeader ?: return
        activeSink?.close()
        activeSink = null

        CrashFileSystem.delete(filePath(stem = stem, suffix = OLD_LOG_SUFFIX))
        CrashFileSystem.rename(
            fromPath = filePath(stem = stem, suffix = LOG_SUFFIX),
            toPath = filePath(stem = stem, suffix = OLD_LOG_SUFFIX),
        )

        val sink = CrashFileSystem.openAppend(filePath(stem = stem, suffix = LOG_SUFFIX)) ?: return
        if (!sink.append(CrashSessionSerializer.encodeStreamPrefix(header))) {
            sink.close()
            return
        }
        activeSink = sink
        recordsInSegment = 0
    }

    private fun applyRetention() {
        val keepCount = (maxSessions - 1).coerceAtLeast(0)
        pastStems().drop(keepCount).forEach { parsed -> deleteSessionFiles(parsed.stem) }
    }

    /** Past session stems (active excluded), newest first. */
    private fun pastStems(): List<ParsedStem> {
        val active = activeStem
        return CrashFileSystem.listFileNames(directoryPath)
            .filter { name -> name.endsWith(LOG_SUFFIX) }
            .mapNotNull { name -> parseStem(name.removeSuffix(LOG_SUFFIX)) }
            .filterNot { parsed -> parsed.stem == active }
            .sortedByDescending { parsed -> parsed.startedAtMs }
    }

    private fun findStem(id: String): String? {
        val activeParsed = activeStem?.let(::parseStem)
        if (activeParsed?.id == id) return activeParsed.stem
        return pastStems().firstOrNull { parsed -> parsed.id == id }?.stem
    }

    private fun deleteSessionFiles(stem: String) {
        CrashFileSystem.delete(filePath(stem = stem, suffix = LOG_SUFFIX))
        CrashFileSystem.delete(filePath(stem = stem, suffix = OLD_LOG_SUFFIX))
        CrashFileSystem.delete(filePath(stem = stem, suffix = CRASH_SUFFIX))
        CrashFileSystem.delete(filePath(stem = stem, suffix = STATE_SUFFIX))
    }

    private fun readSidecar(
        stem: String,
        suffix: String,
    ): String? {
        return CrashFileSystem.readBytes(filePath(stem = stem, suffix = suffix))?.decodeToString()
    }

    private fun parseStem(stem: String): ParsedStem? {
        val startedAtMs = stem.substringBefore(STEM_SEPARATOR).toLongOrNull() ?: return null
        val id = stem.substringAfter(STEM_SEPARATOR, missingDelimiterValue = "")
        if (id.isEmpty()) return null
        return ParsedStem(stem = stem, id = id, startedAtMs = startedAtMs)
    }

    private fun filePath(
        stem: String,
        suffix: String,
    ): String {
        return "$directoryPath/$stem$suffix"
    }

    private data class ParsedStem(
        val stem: String,
        val id: String,
        val startedAtMs: Long,
    )

    companion object {
        const val DEFAULT_MAX_SESSIONS: Int = 10
        const val DEFAULT_MAX_RECORDS_PER_SEGMENT: Int = 500

        private const val STEM_SEPARATOR = "__"
        private const val LOG_SUFFIX = ".log"
        private const val OLD_LOG_SUFFIX = ".log.old"
        private const val CRASH_SUFFIX = ".crash"
        private const val STATE_SUFFIX = ".state"
    }
}
