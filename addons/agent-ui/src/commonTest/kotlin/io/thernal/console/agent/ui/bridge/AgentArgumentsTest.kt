package io.thernal.console.agent.ui.bridge

import io.thernal.console.agent.AgentArg
import io.thernal.console.agent.AgentToolScope
import io.thernal.console.agent.agentEntries
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AgentArgumentsTest {
    private lateinit var id: AgentArg<String>
    private lateinit var limit: AgentArg<Int>
    private lateinit var mode: AgentArg<String?>

    private val tool = agentEntries(id = "t", title = "T", about = "T") {
        tool(name = "find", description = "Find") {
            declare()
            run { "" }
        }
    }.tools.single()

    @Test
    fun `defaults fill omitted arguments and choices match without case`() {
        val call = AgentArguments.parse(tool.params, args("""{"id": "a1", "mode": "FAST"}""")).getOrThrow()

        assertEquals("a1", call[id])
        assertEquals(DEFAULT_LIMIT, call[limit])
        assertEquals("fast", call[mode])
    }

    @Test
    fun `a missing required argument and an out of range number are reported`() {
        val missing = AgentArguments.parse(tool.params, args("""{"limit": 3}"""))
        val outOfRange = AgentArguments.parse(tool.params, args("""{"id": "a1", "limit": 99}"""))

        assertEquals("Missing required argument \"id\"", missing.exceptionOrNull()?.message)
        assertTrue(outOfRange.exceptionOrNull()?.message.orEmpty().contains("1..10"))
    }

    private fun AgentToolScope.declare() {
        id = text(key = "id", description = "Id")
        limit = int(key = "limit", description = "Limit", default = DEFAULT_LIMIT, min = 1, max = 10)
        mode = optionalChoice(key = "mode", description = "Mode", options = listOf("fast", "full"))
    }

    private fun args(json: String): JsonObject {
        return Json.parseToJsonElement(json).jsonObject
    }

    private companion object {
        const val DEFAULT_LIMIT = 5
    }
}
