package io.thernal.console.agent

import io.thernal.console.core.log.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

/**
 * Builds what an addon shares with AI agents, for [AgentRegistry.register]:
 *
 * ```
 * agentEntries(id = "crash-report", title = "Crash report", about = "Crashes from earlier runs") {
 *     tool(name = "crash_sessions", description = "Recent crash sessions, newest first") {
 *         run { store.sessions().joinToString("\n") { it.summaryLine() } }
 *     }
 * }
 * ```
 *
 * [about] is one sentence the hub adds to the MCP server instructions, so the agent knows what the app offers.
 */
fun agentEntries(
    id: String,
    title: String,
    about: String,
    block: AgentEntriesScope.() -> Unit,
): AgentEntries {
    return AgentEntriesBuilder(id).apply(block).build(title = title, about = about)
}

private class AgentEntriesBuilder(private val id: String) : AgentEntriesScope {
    private val tools = mutableListOf<AgentTool>()
    private val feeds = mutableListOf<AgentFeed>()
    private val formats = mutableListOf<AgentLogFormat<*>>()
    private val redactedKeys = mutableSetOf<String>()

    override fun tool(
        name: String,
        description: String,
        block: AgentToolScope.() -> Unit,
    ) {
        addTool(name = name, description = description, isAction = false, block = block)
    }

    override fun action(
        name: String,
        description: String,
        block: AgentToolScope.() -> Unit,
    ) {
        addTool(name = name, description = description, isAction = true, block = block)
    }

    override fun feed(
        name: String,
        description: String,
        records: Flow<AgentRecord>,
    ) {
        requireName(name)
        feeds += AgentFeed(name = name, description = description, records = records)
    }

    override fun logFeed(
        name: String,
        description: String,
        logs: Flow<Log>,
    ) {
        feed(name = name, description = description, records = logs.map(AgentRegistry::recordOf))
    }

    override fun <T : Log> describe(
        type: KClass<T>,
        summary: (T) -> String,
        detail: (T) -> String?,
        fields: (T) -> Map<String, String>,
    ) {
        require(formats.none { it.type == type }) { "Entries \"$id\" describe ${type.simpleName} twice" }
        formats += AgentLogFormat(type = type, summary = summary, detail = detail, fields = fields)
    }

    override fun redact(vararg keys: String) {
        keys.mapNotNullTo(redactedKeys) { key -> key.trim().lowercase().ifEmpty { null } }
    }

    fun build(
        title: String,
        about: String,
    ): AgentEntries {
        val entries = AgentEntries(
            id = id,
            title = title,
            about = about,
            tools = tools.toList(),
            feeds = feeds.toList(),
            logFormats = formats.toList(),
            redactedKeys = redactedKeys.toSet(),
        )
        val names = entries.toolNames()
        val duplicate = names.groupBy { it }.entries.firstOrNull { it.value.size > 1 }?.key
        require(duplicate == null) { "Entries \"$id\" use the tool name \"$duplicate\" twice" }
        return entries
    }

    private fun addTool(
        name: String,
        description: String,
        isAction: Boolean,
        block: AgentToolScope.() -> Unit,
    ) {
        requireName(name)
        tools += AgentToolBuilder(name).apply(block).build(description = description, isAction = isAction)
    }

    private fun requireName(name: String) {
        require(name.matches(AgentNames.NAME_PATTERN)) {
            "Entries \"$id\": invalid name \"$name\" (use lower_snake_case)"
        }
    }
}
