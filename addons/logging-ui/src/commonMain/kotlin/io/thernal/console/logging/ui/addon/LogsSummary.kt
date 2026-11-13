package io.thernal.console.logging.ui.addon

import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import io.thernal.console.logging.ui.ConsoleLogObserver

private const val MAX_TAG_LINES = 30
private const val NO_TAG = "(no tag)"
private const val NO_KIND = "Log"

/**
 * The `logs_summary` answer: whether logging is on, then the logs kept in the app per level, kind and tag. Kinds are
 * class names, the same values the hub's `kind` filter matches.
 */
internal fun logsSummary(
    logs: List<Log>,
    config: ConsoleLogObserver.Config,
): String {
    val lines = mutableListOf<String>()
    val state = if (config.enabled) "on" else "off"
    lines += "Logging $state: ${logs.size} logs kept in the app, up to ${config.maxLogCount}."
    if (!config.enabled) {
        lines += "New logs are dropped, in the app and for agents, until `enabled` is turned on in the Logging " +
            "settings (section \"logging\")."
    }
    if (logs.isNotEmpty()) {
        lines += "Levels: " + levelCounts(logs)
        lines += "Kinds: " + counts(logs) { it::class.simpleName ?: NO_KIND }
        lines += tagLines(logs)
    }
    return lines.joinToString(separator = "\n")
}

private fun levelCounts(logs: List<Log>): String {
    val byLevel = logs.groupingBy { it.level }.eachCount()
    return LogLevel.entries.reversed()
        .mapNotNull { level -> byLevel[level]?.let { count -> "${level.name} $count" } }
        .joinToString()
}

private fun counts(
    logs: List<Log>,
    key: (Log) -> String,
): String {
    return logs.groupingBy(key).eachCount().entries
        .sortedByDescending { it.value }
        .joinToString { (name, count) -> "$name $count" }
}

private fun tagLines(logs: List<Log>): List<String> {
    val byTag = logs.groupBy { it.tag?.takeIf(String::isNotBlank) }.entries.sortedByDescending { it.value.size }
    val shown = byTag.take(MAX_TAG_LINES).map { (tag, tagged) ->
        val highest = tagged.maxOf { it.level }
        val suffix = if (highest == LogLevel.None) "" else ", highest ${highest.name}"
        "${tag ?: NO_TAG} ${tagged.size}$suffix"
    }
    val hidden = byTag.size - shown.size
    val more = if (hidden > 0) listOf("… $hidden more tags") else emptyList()
    return listOf("Tags:") + shown + more
}
