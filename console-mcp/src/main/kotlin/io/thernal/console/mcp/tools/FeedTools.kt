package io.thernal.console.mcp.tools

import io.thernal.console.mcp.hub.DeviceStore
import io.thernal.console.mcp.hub.HubDevice
import io.thernal.console.mcp.hub.HubRecord
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

/** The three tools the hub derives from every feed: a filtered list, one entry in full, and waiting for an entry. */
class FeedTools(private val store: DeviceStore) {
    fun list(
        feed: String,
        arguments: JsonObject,
    ): ToolText {
        val device = resolve(feed, arguments) ?: return problem(feed, arguments)
        val limit = (arguments.int(ToolSchemas.LIMIT) ?: ToolSchemas.DEFAULT_LIMIT).coerceIn(1, ToolSchemas.MAX_LIMIT)
        val matching = store.records(device.info.id, feed).filter(query(arguments)::matches)
        val shown = matching.takeLast(limit)
        val header = "${device.title()} · $feed: showing ${shown.size} of ${matching.size} matching" +
            (shown.lastOrNull()?.let { " · last seq ${it.seq}" } ?: "")
        return ToolText(header + "\n" + shown.joinToString(separator = "\n") { it.toLine() })
    }

    fun get(
        feed: String,
        arguments: JsonObject,
    ): ToolText {
        val device = resolve(feed, arguments) ?: return problem(feed, arguments)
        val key = arguments.string(ToolSchemas.ID)?.removePrefix("#") ?: return ToolText("Pass `id`.", isError = true)
        val record = store.records(device.info.id, feed).lastOrNull { it.id == key || it.seq.toString() == key }
            ?: return ToolText("No $feed entry \"$key\" on ${device.info.name}.", isError = true)
        return ToolText(record.toDetail())
    }

    suspend fun wait(
        feed: String,
        arguments: JsonObject,
    ): ToolText {
        val device = resolve(feed, arguments) ?: return problem(feed, arguments)
        val seconds = (arguments.int(ToolSchemas.TIMEOUT) ?: ToolSchemas.DEFAULT_WAIT_SECONDS)
            .coerceIn(1, ToolSchemas.MAX_WAIT_SECONDS)
        val after = arguments.long(ToolSchemas.AFTER) ?: store.records(device.info.id, feed).lastOrNull()?.seq ?: -1L
        val query = query(arguments).copy(after = after)
        val found = withTimeoutOrNull(seconds * MILLIS_PER_SECOND) {
            var hits: List<HubRecord> = emptyList()
            store.recordsVersion.first {
                hits = store.records(device.info.id, feed).filter(query::matches)
                hits.isNotEmpty()
            }
            hits
        }
        return if (found == null) {
            ToolText("No matching $feed entry on ${device.info.name} within $seconds s (after seq $after).")
        } else {
            ToolText(found.take(WAIT_RESULT_LIMIT).joinToString(separator = "\n") { it.toLine() })
        }
    }

    private fun resolve(
        feed: String,
        arguments: JsonObject,
    ): HubDevice? {
        return (choose(feed, arguments) as? DeviceChoice.Found)?.device
    }

    private fun problem(
        feed: String,
        arguments: JsonObject,
    ): ToolText {
        return ToolText((choose(feed, arguments) as DeviceChoice.Problem).message, isError = true)
    }

    private fun choose(
        feed: String,
        arguments: JsonObject,
    ): DeviceChoice {
        return DeviceChoice.resolve(store.devices(), arguments.string(ToolSchemas.DEVICE)) { it.hasFeed(feed) }
    }

    private fun query(arguments: JsonObject): RecordQuery {
        return RecordQuery(
            minLevel = arguments.string(ToolSchemas.LEVEL),
            tag = arguments.string(ToolSchemas.TAG),
            kind = arguments.string(ToolSchemas.KIND),
            text = arguments.string(ToolSchemas.QUERY),
            after = arguments.long(ToolSchemas.AFTER),
        )
    }

    private fun HubDevice.title(): String {
        return "${info.name} (${info.platform}, ${if (connected) "connected" else "disconnected"})"
    }

    private fun JsonObject.string(key: String): String? {
        return this[key]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
    }

    private fun JsonObject.int(key: String): Int? {
        return this[key]?.jsonPrimitive?.let { it.intOrNull ?: it.contentOrNull?.toIntOrNull() }
    }

    private fun JsonObject.long(key: String): Long? {
        return this[key]?.jsonPrimitive?.let { it.longOrNull ?: it.contentOrNull?.toLongOrNull() }
    }

    private companion object {
        const val MILLIS_PER_SECOND = 1_000L
        const val WAIT_RESULT_LIMIT = 20
    }
}
