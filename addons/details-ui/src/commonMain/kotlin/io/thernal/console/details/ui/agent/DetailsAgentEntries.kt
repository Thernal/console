package io.thernal.console.details.ui.agent

import io.thernal.console.agent.AgentEntries
import io.thernal.console.agent.AgentRecord
import io.thernal.console.agent.agentEntries
import io.thernal.console.details.ConsoleDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock

private const val DETAILS_TAG = "Details"
private const val CHANGE_KIND = "DetailChange"
private const val NONE = "(none)"

/** What the details addon shares with an AI agent: the live key/value pairs, and each change to them. */
internal fun detailsAgentEntries(details: Flow<Map<String, String>> = ConsoleDetails.flow): AgentEntries {
    return agentEntries(
        id = "details",
        title = "Details",
        about = "Live key/value facts the app publishes: session, user, flags, build",
    ) {
        tool(name = "details", description = "Current key = value pairs, in the order they were first set") {
            val query = optionalText(key = "query", description = "Only keys or values containing this")
            run { call -> detailsText(ConsoleDetails.flow.value, call[query]) }
        }
        feed(
            name = "details_changes",
            description = "Each change to a detail (set, changed, removed); the first records are the values at start",
            records = detailChanges(details),
        )
    }
}

internal fun detailsText(
    details: Map<String, String>,
    query: String?,
): String {
    if (details.isEmpty()) return "The app has published no details."
    val shown = details.filter { (key, value) ->
        query.isNullOrBlank() || key.contains(query, ignoreCase = true) || value.contains(query, ignoreCase = true)
    }
    if (shown.isEmpty()) return "No detail matches \"$query\"; ${details.size} details in all."
    return shown.entries.joinToString(separator = "\n") { (key, value) -> "$key = $value" }
}

/** One record per key that was set, changed or removed between two values of [details]. */
internal fun detailChanges(details: Flow<Map<String, String>>): Flow<AgentRecord> {
    return flow {
        var previous = emptyMap<String, String>()
        var count = 0L
        details.collect { current ->
            val keys = previous.keys + current.keys
            keys.filter { previous[it] != current[it] }.forEach { key ->
                emit(changeRecord(key = key, old = previous[key], new = current[key], number = count++))
            }
            previous = current
        }
    }
}

private fun changeRecord(
    key: String,
    old: String?,
    new: String?,
    number: Long,
): AgentRecord {
    val summary = if (new == null) "$key removed (was ${old ?: NONE})" else "$key: ${old ?: NONE} → $new"
    return AgentRecord(
        id = "detail-$number",
        timeMillis = Clock.System.now().toEpochMilliseconds(),
        summary = summary,
        tag = DETAILS_TAG,
        kind = CHANGE_KIND,
        fields = buildMap {
            put("key", key)
            old?.let { put("old", it) }
            new?.let { put("new", it) }
        },
    )
}
