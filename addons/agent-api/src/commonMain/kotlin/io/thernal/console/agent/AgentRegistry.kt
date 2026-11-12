package io.thernal.console.agent

import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.core.log.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Central registry of what addons share with AI agents. Each addon registers its [AgentEntries] in `onInstall`; the
 * agent bridge reads [entries] and keeps the hub up to date, so addon install order never matters.
 */
object AgentRegistry {
    private val _entries = MutableStateFlow<List<AgentEntries>>(emptyList())
    val entries: StateFlow<List<AgentEntries>> = _entries.asStateFlow()

    /** Registers [entries]. Fails fast on a duplicate id or a tool name another addon already uses. */
    @ConsoleInternalApi
    fun register(entries: AgentEntries) {
        _entries.update { current ->
            check(current.none { it.id == entries.id }) { "Agent entries id \"${entries.id}\" already registered" }
            val taken = current.flatMap { it.toolNames() }.toSet() + AgentNames.RESERVED
            val clash = entries.toolNames().firstOrNull { it in taken }
            check(clash == null) { "Agent tool name \"$clash\" already registered" }
            current + entries
        }
    }

    /** [log] as an agent sees it: through the format registered for its exact type, or the default one. */
    internal fun recordOf(log: Log): AgentRecord {
        val format = _entries.value.firstNotNullOfOrNull { entries ->
            entries.logFormats.firstOrNull { it.type == log::class }
        }
        return format?.record(log) ?: AgentLogFormat.defaultRecord(log)
    }

    /** Every key some addon asked to redact. */
    @ConsoleInternalApi
    fun redactedKeys(): Set<String> {
        return _entries.value.flatMapTo(mutableSetOf()) { it.redactedKeys }
    }

    /** Test-only: clears every registration. */
    internal fun resetForTest() {
        _entries.value = emptyList()
    }
}
