package io.thernal.console.agent.ui.bridge

import io.thernal.console.agent.AgentRecord
import io.thernal.console.agent.agentEntries
import io.thernal.console.agent.ui.device.AgentDevice
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlin.test.Test
import kotlin.test.assertEquals

class AgentMessagesTest {
    private val entries = agentEntries(id = "demo", title = "Demo", about = "Demo app") {
        tool(name = "state", description = "State") {
            val limit = int(key = "limit", description = "Limit", default = 5, min = 1, max = 9)
            run { call -> "limit ${call[limit]}" }
        }
        action(name = "reset", description = "Reset") { run { "done" } }
        feed(name = "events", description = "Events", records = emptyFlow())
    }

    @Test
    fun `manifest leaves actions out while they are off`() {
        val off = parse(AgentMessages.manifest(entries = listOf(entries), enableActions = false))
        val on = parse(AgentMessages.manifest(entries = listOf(entries), enableActions = true))

        assertEquals(listOf("state"), toolNames(off))
        assertEquals(listOf("state", "reset"), toolNames(on))
        assertEquals("events", section(on)["feeds"]!!.jsonArray.single().jsonObject["name"]!!.jsonPrimitive.content)
    }

    @Test
    fun `manifest describes params with type and range`() {
        val param = section(parse(AgentMessages.manifest(entries = listOf(entries), enableActions = false)))
            .getValue("tools").jsonArray.single().jsonObject
            .getValue("params").jsonArray.single().jsonObject

        assertEquals("integer", param.getValue("type").jsonPrimitive.content)
        assertEquals(5, param.getValue("default").jsonPrimitive.int)
        assertEquals(9, param.getValue("max").jsonPrimitive.int)
        assertEquals(false, param.getValue("required").jsonPrimitive.boolean)
    }

    @Test
    fun `record carries the feed and sequence`() {
        val record = AgentRecord(id = "r1", timeMillis = TIME, summary = "Saved", fields = mapOf("count" to "2"))

        val json = parse(AgentMessages.record(feed = "events", seq = 7, record = record))

        assertEquals("events", json.getValue("feed").jsonPrimitive.content)
        assertEquals(7, json.getValue("seq").jsonPrimitive.long)
        assertEquals(TIME, json.getValue("time").jsonPrimitive.long)
        assertEquals(JsonNull, json.getValue("detail"))
        assertEquals("2", json.getValue("fields").jsonObject.getValue("count").jsonPrimitive.content)
    }

    @Test
    fun `hello carries the protocol version and the device with its run`() {
        val device = AgentDevice(
            id = "d1",
            runId = "r1",
            name = "Pixel",
            platform = "Android",
            osVersion = "16",
            appId = null,
        )

        val json = parse(AgentMessages.hello(device))

        assertEquals(AgentMessages.PROTOCOL_VERSION, json.getValue("protocol").jsonPrimitive.int)
        assertEquals(JsonNull, json.getValue("device").jsonObject.getValue("app"))
        assertEquals("r1", json.getValue("device").jsonObject.getValue("run").jsonPrimitive.content)
    }

    private fun parse(text: String): JsonObject {
        return Json.parseToJsonElement(text).jsonObject
    }

    private fun section(manifest: JsonObject): JsonObject {
        return manifest.getValue("sections").jsonArray.single().jsonObject
    }

    private fun toolNames(manifest: JsonObject): List<String> {
        val tools = section(manifest).getValue("tools").jsonArray
        return tools.map { it.jsonObject.getValue("name").jsonPrimitive.content }
    }

    private companion object {
        const val TIME = 1_700_000_000_123L
    }
}
