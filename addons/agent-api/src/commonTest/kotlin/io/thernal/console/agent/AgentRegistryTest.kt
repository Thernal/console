@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.agent

import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.core.log.BasicLog
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class AgentRegistryTest {
    @AfterTest
    fun tearDown() {
        AgentRegistry.resetForTest()
    }

    @Test
    fun `a tool reads typed arguments and enum choices`(): TestResult {
        return runTest {
            val entries = agentEntries(id = "demo", title = "Demo", about = "Demo") {
                tool(name = "echo", description = "Echo") {
                    val text = text(key = "text", description = "Text")
                    val times = int(key = "times", description = "Times", default = 1, min = 1, max = 3)
                    val level = enum(key = "level", description = "Level", default = LogLevel.Info)
                    run { call -> "${call[text].repeat(call[times])} ${call[level].name}" }
                }
            }
            val tool = entries.tools.single()

            val result = tool.run(AgentCall(mapOf("text" to "ab", "times" to 2, "level" to "Error")))

            assertEquals("abab Error", result)
            assertEquals(listOf("text", "times", "level"), tool.params.map { it.key })
            assertEquals(true, tool.params.first().isRequired)
        }
    }

    @Test
    fun `register rejects a duplicate id and a taken tool name`() {
        AgentRegistry.register(entries(id = "one", toolName = "state"))

        assertFailsWith<IllegalStateException> { AgentRegistry.register(entries(id = "one", toolName = "other")) }
        assertFailsWith<IllegalStateException> { AgentRegistry.register(entries(id = "two", toolName = "state")) }
        assertFailsWith<IllegalStateException> { AgentRegistry.register(entries(id = "three", toolName = "devices")) }
    }

    @Test
    fun `a feed occupies the names the hub derives from it`() {
        AgentRegistry.register(
            agentEntries(id = "logging", title = "Logs", about = "Logs") {
                feed(name = "logs", description = "Logs", records = emptyFlow())
            },
        )

        assertFailsWith<IllegalStateException> { AgentRegistry.register(entries(id = "x", toolName = "logs_wait")) }
    }

    @Test
    fun `invalid names fail while building`() {
        assertFailsWith<IllegalArgumentException> { entries(id = "x", toolName = "Bad-Name") }
    }

    @Test
    fun `recordOf uses the format registered for the exact type`() {
        AgentRegistry.register(
            agentEntries(id = "ping", title = "Ping", about = "Ping") {
                describe<PingLog>(summary = { "ping ${it.millis} ms" }, fields = { mapOf("millis" to "${it.millis}") })
            },
        )

        val ping = AgentRegistry.recordOf(PingLog(millis = 42))
        val basic = AgentRegistry.recordOf(BasicLog(message = "first\nsecond", tag = "T"))

        assertEquals("ping 42 ms", ping.summary)
        assertEquals(mapOf("millis" to "42"), ping.fields)
        assertEquals("first", basic.summary)
        assertEquals("BasicLog", basic.kind)
    }

    private fun entries(
        id: String,
        toolName: String,
    ): AgentEntries {
        return agentEntries(id = id, title = id, about = id) {
            tool(name = toolName, description = toolName) { run { toolName } }
        }
    }

    private class PingLog(val millis: Int) : Log {
        override val id: String = "ping"
        override val tag: String? = null
        override val level: LogLevel = LogLevel.Debug
        override val message: String = "ping"
        override val groupId: String? = null
        override val timestamp: Instant = Instant.fromEpochMilliseconds(0)
    }
}
