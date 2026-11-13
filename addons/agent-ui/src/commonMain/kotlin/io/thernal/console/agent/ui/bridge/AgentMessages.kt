package io.thernal.console.agent.ui.bridge

import io.thernal.console.agent.AgentEntries
import io.thernal.console.agent.AgentFeed
import io.thernal.console.agent.AgentParam
import io.thernal.console.agent.AgentRecord
import io.thernal.console.agent.AgentTool
import io.thernal.console.agent.ui.device.AgentDevice
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Messages between the app and the hub, as JSON text. App → hub: `hello` once per connection, `manifest` whenever
 * the shared tools change, `record` per feed entry, `result` per answered call. Hub → app: `call`.
 */
internal object AgentMessages {
    const val PROTOCOL_VERSION = 2
    const val TYPE_CALL = "call"
    private const val TYPE_HELLO = "hello"
    private const val TYPE_MANIFEST = "manifest"
    private const val TYPE_RECORD = "record"
    private const val TYPE_RESULT = "result"

    fun hello(device: AgentDevice): String {
        val deviceJson = obj(
            "id" to JsonPrimitive(device.id),
            "run" to JsonPrimitive(device.runId),
            "name" to JsonPrimitive(device.name),
            "platform" to JsonPrimitive(device.platform),
            "os" to JsonPrimitive(device.osVersion),
            "app" to JsonPrimitive(device.appId),
        )
        return obj(
            "type" to JsonPrimitive(TYPE_HELLO),
            "protocol" to JsonPrimitive(PROTOCOL_VERSION),
            "device" to deviceJson,
        ).toString()
    }

    /** What the app shares; actions are left out while [enableActions] is off, so the agent never sees them. */
    fun manifest(
        entries: List<AgentEntries>,
        enableActions: Boolean,
    ): String {
        val sections = entries.map { section ->
            obj(
                "id" to JsonPrimitive(section.id),
                "title" to JsonPrimitive(section.title),
                "about" to JsonPrimitive(section.about),
                "tools" to JsonArray(section.tools.filter { enableActions || !it.isAction }.map(::tool)),
                "feeds" to JsonArray(section.feeds.map(::feed)),
            )
        }
        return obj(
            "type" to JsonPrimitive(TYPE_MANIFEST),
            "actions" to JsonPrimitive(enableActions),
            "sections" to JsonArray(sections),
        ).toString()
    }

    fun record(
        feed: String,
        seq: Long,
        record: AgentRecord,
    ): String {
        return obj(
            "type" to JsonPrimitive(TYPE_RECORD),
            "feed" to JsonPrimitive(feed),
            "seq" to JsonPrimitive(seq),
            "id" to JsonPrimitive(record.id),
            "time" to JsonPrimitive(record.timeMillis),
            "summary" to JsonPrimitive(record.summary),
            "detail" to JsonPrimitive(record.detail),
            "level" to JsonPrimitive(record.level),
            "tag" to JsonPrimitive(record.tag),
            "kind" to JsonPrimitive(record.kind),
            "fields" to JsonObject(record.fields.mapValues { JsonPrimitive(it.value) }),
        ).toString()
    }

    fun result(
        id: Long,
        isError: Boolean,
        text: String,
    ): String {
        return obj(
            "type" to JsonPrimitive(TYPE_RESULT),
            "id" to JsonPrimitive(id),
            "error" to JsonPrimitive(isError),
            "text" to JsonPrimitive(text),
        ).toString()
    }

    private fun tool(tool: AgentTool): JsonObject {
        return obj(
            "name" to JsonPrimitive(tool.name),
            "description" to JsonPrimitive(tool.description),
            "action" to JsonPrimitive(tool.isAction),
            "params" to JsonArray(tool.params.map(::param)),
        )
    }

    private fun feed(feed: AgentFeed): JsonObject {
        return obj("name" to JsonPrimitive(feed.name), "description" to JsonPrimitive(feed.description))
    }

    private fun param(param: AgentParam): JsonObject {
        val common = listOf(
            "key" to JsonPrimitive(param.key),
            "description" to JsonPrimitive(param.description),
            "required" to JsonPrimitive(param.isRequired),
        )
        val specific = when (param) {
            is AgentParam.Text -> listOf("type" to JsonPrimitive("text"), "default" to JsonPrimitive(param.default))

            is AgentParam.Integer -> listOf(
                "type" to JsonPrimitive("integer"),
                "default" to JsonPrimitive(param.default),
                "min" to param.min.takeIf { it != Int.MIN_VALUE }.toJson(),
                "max" to param.max.takeIf { it != Int.MAX_VALUE }.toJson(),
            )

            is AgentParam.Toggle -> listOf(
                "type" to JsonPrimitive("boolean"),
                "default" to JsonPrimitive(param.default),
            )

            is AgentParam.Choice -> listOf(
                "type" to JsonPrimitive("choice"),
                "options" to JsonArray(param.options.map { JsonPrimitive(it) }),
                "default" to JsonPrimitive(param.default),
            )
        }
        return JsonObject((common + specific).toMap())
    }

    private fun Int?.toJson(): JsonElement {
        return this?.let(::JsonPrimitive) ?: JsonNull
    }

    private fun obj(vararg pairs: Pair<String, JsonElement>): JsonObject {
        return JsonObject(pairs.toMap())
    }
}
