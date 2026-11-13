package io.thernal.console.mcp.tools

import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import io.modelcontextprotocol.kotlin.sdk.types.ToolAnnotations
import io.thernal.console.mcp.hub.DeviceStore
import io.thernal.console.mcp.hub.HubManifest
import io.thernal.console.mcp.hub.HubProtocol
import io.thernal.console.mcp.hub.HubServer
import io.thernal.console.mcp.hub.HubStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * Keeps the MCP tool list in step with what connected apps share. `devices` is built in; every feed adds a list, a
 * get and a wait tool; every app tool is forwarded to the app that offers it. Adding or removing a tool makes the SDK
 * send `notifications/tools/list_changed`. `devices` starts with the hub's [version], the Console release it came with.
 */
class HubTools(
    private val server: Server,
    private val store: DeviceStore,
    private val hub: HubServer,
    private val version: String,
) {
    private val feedTools = FeedTools(store)
    private val installed = mutableMapOf<String, String>()

    fun registerBuiltIns() {
        server.addTool(
            name = DEVICES,
            description = "Lists the apps connected to the Console hub and what each one shares " +
                "(tools, feeds, actions).",
            toolAnnotations = readOnly(),
        ) {
            awaitFirstDevice()
            val hubLine = "console-mcp $version · protocol ${HubProtocol.CURRENT}\n"
            result(ToolText(hubNote() + hubLine + DeviceChoice.describe(store.devices()).ifEmpty { "No devices yet." }))
        }
    }

    suspend fun keepInSync() {
        store.shape.collect { sync() }
    }

    @Synchronized
    fun sync() {
        val wanted = wantedTools()
        (installed.keys - wanted.keys).forEach { name ->
            server.removeTool(name)
            installed.remove(name)
        }
        wanted.forEach { (name, tool) ->
            if (installed[name] == tool.signature) return@forEach
            if (name in installed) server.removeTool(name)
            tool.register()
            installed[name] = tool.signature
        }
    }

    private fun wantedTools(): Map<String, WantedTool> {
        val devices = store.devices()
        val wanted = linkedMapOf<String, WantedTool>()
        devices.flatMap { it.manifest.feeds }.distinctBy { it.name }.forEach { feed ->
            feedToolsOf(feed).forEach { wanted.putIfAbsent(it.first, it.second) }
        }
        devices.filter { it.connected }.flatMap { it.manifest.tools }.forEach { tool ->
            if (tool.name != DEVICES) wanted.putIfAbsent(tool.name, appTool(tool))
        }
        return wanted
    }

    private fun feedToolsOf(feed: HubManifest.Feed): List<Pair<String, WantedTool>> {
        val name = feed.name
        val list = WantedTool("feed:$name:${feed.description}") {
            server.addTool(
                name = name,
                description = "${feed.description}. Oldest first, one line each (#seq time LEVEL [tag] summary); " +
                    "pass `after` with the last seq you saw to read only newer entries, `${name}_get` for one in full.",
                inputSchema = ToolSchemas.feedList(),
                toolAnnotations = readOnly(),
            ) { request -> result(feedTools.list(name, request.arguments.orEmpty())) }
        }
        val get = WantedTool("feed-get:$name") {
            server.addTool(
                name = "${name}_get",
                description = "One $name entry in full: fields and the whole text.",
                inputSchema = ToolSchemas.feedGet(),
                toolAnnotations = readOnly(),
            ) { request -> result(feedTools.get(name, request.arguments.orEmpty())) }
        }
        val wait = WantedTool("feed-wait:$name") {
            server.addTool(
                name = "${name}_wait",
                description = "Waits until a new $name entry matching the filters arrives, e.g. after relaunching " +
                    "the app or triggering a flow, and returns it.",
                inputSchema = ToolSchemas.feedWait(),
                toolAnnotations = readOnly(),
            ) { request -> result(feedTools.wait(name, request.arguments.orEmpty())) }
        }
        return listOf(name to list, "${name}_get" to get, "${name}_wait" to wait)
    }

    private fun appTool(tool: HubManifest.Tool): WantedTool {
        return WantedTool("app:$tool") {
            server.addTool(
                name = tool.name,
                description = (if (tool.action) "[Action: changes the app] " else "") + tool.description,
                inputSchema = ToolSchemas.forParams(tool.params),
                toolAnnotations = if (tool.action) action() else readOnly(),
            ) { request -> result(callApp(tool.name, request.arguments.orEmpty())) }
        }
    }

    private suspend fun callApp(
        name: String,
        arguments: JsonObject,
    ): ToolText {
        val device = when (
            val choice = DeviceChoice.resolve(store.devices(), arguments.device()) { it.connected && it.hasTool(name) }
        ) {
            is DeviceChoice.Found -> choice.device
            is DeviceChoice.Problem -> return ToolText(choice.message, isError = true)
        }
        val link = store.link(device.info.id)
            ?: return ToolText("${device.info.name} is not connected.", isError = true)
        val forwarded = JsonObject(arguments - ToolSchemas.DEVICE)
        val answer = link.call(tool = name, arguments = forwarded, timeoutMillis = CALL_TIMEOUT_MILLIS)
        return ToolText(answer.text, isError = answer.isError)
    }

    /** An app retries the hub every two seconds at most; right after a start, wait for it instead of failing. */
    private suspend fun awaitFirstDevice() {
        if (store.devices().isEmpty()) {
            withTimeoutOrNull(FIRST_DEVICE_WAIT_MILLIS) { store.shape.first { store.devices().isNotEmpty() } }
        }
    }

    private fun hubNote(): String {
        val status = hub.status
        return if (status is HubStatus.Failed) {
            "Warning: ${status.reason}. Another console-mcp may own the port.\n"
        } else {
            ""
        }
    }

    private fun JsonObject.device(): String? {
        return this[ToolSchemas.DEVICE]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
    }

    private fun JsonObject?.orEmpty(): JsonObject {
        return this ?: JsonObject(emptyMap())
    }

    private fun result(text: ToolText): CallToolResult {
        return CallToolResult(content = listOf(TextContent(text.text)), isError = text.isError.takeIf { it })
    }

    private fun readOnly(): ToolAnnotations {
        return ToolAnnotations(readOnlyHint = true, openWorldHint = false)
    }

    private fun action(): ToolAnnotations {
        return ToolAnnotations(readOnlyHint = false, destructiveHint = true, openWorldHint = false)
    }

    /** A tool the hub should offer; [signature] tells whether an installed one must be replaced. */
    private class WantedTool(
        val signature: String,
        val register: () -> Unit,
    )

    private companion object {
        const val DEVICES = "devices"
        const val CALL_TIMEOUT_MILLIS = 20_000L
        const val FIRST_DEVICE_WAIT_MILLIS = 3_000L
    }
}
