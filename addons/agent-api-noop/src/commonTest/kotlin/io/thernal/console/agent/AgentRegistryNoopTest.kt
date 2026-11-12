@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.agent

import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.core.log.BasicLog
import io.thernal.console.core.log.LogLevel
import kotlinx.coroutines.flow.emptyFlow
import kotlin.test.Test
import kotlin.test.assertTrue

class AgentRegistryNoopTest {
    // Uses every DSL call `agent-api` offers, so a signature that drifts from it fails to compile here.
    @Test
    fun `entries built with the whole DSL are dropped`() {
        val entries = agentEntries(id = "demo", title = "Demo", about = "Every DSL call") {
            tool(name = "demo_state", description = "State") {
                val text = text(key = "text", description = "Text")
                val prefix = text(key = "prefix", description = "Prefix", default = ">")
                val note = optionalText(key = "note", description = "Note")
                val count = int(key = "count", description = "Count", default = 1, min = 0, max = 9)
                val page = optionalInt(key = "page", description = "Page")
                val verbose = toggle(key = "verbose", description = "Verbose")
                val sort = choice(key = "sort", description = "Sort", options = listOf("asc", "desc"), default = "asc")
                val group = optionalChoice(key = "group", description = "Group", options = listOf("tag"))
                val level = enum(key = "level", description = "Level", default = LogLevel.Info)
                run { call ->
                    listOf(call[text], call[prefix], call[note], call[count], call[page], call[verbose], call[sort])
                        .plus(listOf(call[group], call[level].name.lowercase()))
                        .joinToString()
                }
            }
            action(name = "demo_reset", description = "Reset") { run { "done" } }
            feed(name = "demo_events", description = "Events", records = emptyFlow())
            logFeed(name = "demo_logs", description = "Logs", logs = emptyFlow())
            describe<BasicLog>()
            redact("secret")
        }

        AgentRegistry.register(entries)

        assertTrue(AgentRegistry.entries.value.isEmpty())
        assertTrue(AgentRegistry.redactedKeys().isEmpty())
    }
}
