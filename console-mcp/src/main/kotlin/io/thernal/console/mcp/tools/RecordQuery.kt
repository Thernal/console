package io.thernal.console.mcp.tools

import io.thernal.console.mcp.hub.HubRecord

/** Filters shared by a feed's list and wait tools. [minLevel] is a Console level name; [after] is a seq cursor. */
data class RecordQuery(
    val minLevel: String? = null,
    val tag: String? = null,
    val kind: String? = null,
    val text: String? = null,
    val after: Long? = null,
) {
    fun matches(record: HubRecord): Boolean {
        val minRank = minLevel?.let(::levelRank)
        return (after == null || record.seq > after) &&
            (minRank == null || levelRank(record.level.orEmpty()) >= minRank) &&
            (tag == null || record.tag.equals(tag, ignoreCase = true)) &&
            (kind == null || record.kind.equals(kind, ignoreCase = true)) &&
            (text == null || containsText(record = record, text = text))
    }

    private fun containsText(
        record: HubRecord,
        text: String,
    ): Boolean {
        return record.summary.contains(text, ignoreCase = true) ||
            record.detail.orEmpty().contains(text, ignoreCase = true)
    }

    companion object {
        /** Console's `LogLevel` names, lowest first. */
        val LEVELS = listOf("None", "Verbose", "Debug", "Info", "Success", "Warning", "Error", "Fatal")

        fun levelRank(level: String): Int {
            return LEVELS.indexOfFirst { it.equals(level, ignoreCase = true) }.coerceAtLeast(0)
        }
    }
}
