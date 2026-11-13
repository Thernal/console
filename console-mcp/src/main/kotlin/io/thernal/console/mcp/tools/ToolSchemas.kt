package io.thernal.console.mcp.tools

import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import io.thernal.console.mcp.hub.HubManifest
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/** Input schemas of the hub's tools. Every tool takes an optional `device`. */
object ToolSchemas {
    const val DEVICE = "device"
    const val LEVEL = "level"
    const val TAG = "tag"
    const val KIND = "kind"
    const val QUERY = "query"
    const val AFTER = "after"
    const val LIMIT = "limit"
    const val ID = "id"
    const val TIMEOUT = "timeout_seconds"

    const val DEFAULT_LIMIT = 50
    const val MAX_LIMIT = 500
    const val DEFAULT_WAIT_SECONDS = 10
    const val MAX_WAIT_SECONDS = 60

    fun forParams(params: List<HubManifest.Param>): ToolSchema {
        val properties = listOf(device()) + params.map { it.key to param(it) }
        return ToolSchema(
            properties = JsonObject(properties.toMap()),
            required = params.filter { it.required }.map { it.key }.ifEmpty { null },
        )
    }

    fun feedList(): ToolSchema {
        return ToolSchema(
            properties = JsonObject(
                (
                    listOf(device()) + filters() + listOf(
                        LIMIT to integer(
                            "How many of the newest matching entries to return (default $DEFAULT_LIMIT, max $MAX_LIMIT).",
                        ),
                    )
                    ).toMap(),
            ),
        )
    }

    fun feedGet(): ToolSchema {
        return ToolSchema(
            properties = JsonObject(
                mapOf(
                    device(),
                    ID to string("The entry's id, or its #seq number from the list."),
                ),
            ),
            required = listOf(ID),
        )
    }

    fun feedWait(): ToolSchema {
        return ToolSchema(
            properties = JsonObject(
                (
                    listOf(device()) + filters() + listOf(
                        TIMEOUT to integer("How long to wait (default $DEFAULT_WAIT_SECONDS, max $MAX_WAIT_SECONDS)."),
                    )
                    ).toMap(),
            ),
        )
    }

    private fun filters(): List<Pair<String, JsonObject>> {
        return listOf(
            LEVEL to obj(
                "type" to JsonPrimitive("string"),
                "enum" to JsonArray(RecordQuery.LEVELS.map { JsonPrimitive(it) }),
                "description" to JsonPrimitive("Minimum level."),
            ),
            TAG to string("Only entries with this tag (case-insensitive)."),
            KIND to string("Only entries of this kind, e.g. a log type such as NetworkLog."),
            QUERY to string("Only entries whose text contains this (case-insensitive)."),
            AFTER to integer(
                "Only entries with a seq greater than this; for the wait tool, defaults to the newest seq now.",
            ),
        )
    }

    private fun param(param: HubManifest.Param): JsonObject {
        val description =
            param.description + (param.default?.takeUnless { it is JsonNull }?.let { " (default: $it)" } ?: "")
        val entries = mutableListOf<Pair<String, JsonElement>>("description" to JsonPrimitive(description))
        when (param.type) {
            "integer" -> {
                entries += "type" to JsonPrimitive("integer")
                param.min?.let { entries += "minimum" to JsonPrimitive(it) }
                param.max?.let { entries += "maximum" to JsonPrimitive(it) }
            }

            "boolean" -> entries += "type" to JsonPrimitive("boolean")

            "choice" -> {
                entries += "type" to JsonPrimitive("string")
                entries += "enum" to JsonArray(param.options.orEmpty().map { JsonPrimitive(it) })
            }

            else -> entries += "type" to JsonPrimitive("string")
        }
        return JsonObject(entries.toMap())
    }

    private fun device(): Pair<String, JsonObject> {
        return DEVICE to string("Device id or part of its name. Optional when only one connected app offers this tool.")
    }

    private fun string(description: String): JsonObject {
        return obj("type" to JsonPrimitive("string"), "description" to JsonPrimitive(description))
    }

    private fun integer(description: String): JsonObject {
        return obj("type" to JsonPrimitive("integer"), "description" to JsonPrimitive(description))
    }

    private fun obj(vararg pairs: Pair<String, JsonElement>): JsonObject {
        return JsonObject(pairs.toMap())
    }
}
