package io.thernal.console.mcp

import io.github.oshai.kotlinlogging.KotlinLoggingConfiguration
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import io.modelcontextprotocol.kotlin.sdk.types.ServerCapabilities
import io.thernal.console.mcp.hub.DeviceStore
import io.thernal.console.mcp.hub.HubServer
import io.thernal.console.mcp.tools.HubTools
import io.thernal.console.mcp.tools.instructions
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered

private const val SERVER_NAME = "console"
private const val SERVER_VERSION = "0.1.0"
private const val HUB_HOST = "127.0.0.1"
private const val DEFAULT_HUB_PORT = 47_474
private const val PORT_FLAG = "--port"

private const val FIRST_MANIFEST_WAIT_MILLIS = 2_500L

/**
 * Starts the hub (TCP on loopback, for apps) and the MCP server (stdio, for the agent) in one process.
 * Stdout belongs to MCP, so diagnostics go to stderr.
 */
fun main(args: Array<String>) {
    // kotlin-logging (used by the MCP SDK) prints a banner to stdout on first use, which breaks the JSON-RPC stream.
    KotlinLoggingConfiguration.logStartupMessage = false
    val port = args.indexOf(PORT_FLAG).takeIf { it >= 0 }?.let { args.getOrNull(it + 1)?.toIntOrNull() }
        ?: DEFAULT_HUB_PORT
    runBlocking {
        val store = DeviceStore()
        val hub = HubServer(store = store, host = HUB_HOST, port = port)
        val hubJob = launch { hub.run() }

        val server = Server(
            serverInfo = Implementation(name = SERVER_NAME, version = SERVER_VERSION),
            options = ServerOptions(
                capabilities = ServerCapabilities(tools = ServerCapabilities.Tools(listChanged = true)),
            ),
            instructionsProvider = { instructions(store) },
        )
        val tools = HubTools(server = server, store = store, hub = hub)
        tools.registerBuiltIns()
        val syncJob = launch { tools.keepInSync() }
        // Give an app that is already running a moment to connect, so the first tools/list already has its tools.
        withTimeoutOrNull(FIRST_MANIFEST_WAIT_MILLIS) { store.awaitFirstManifest() }
        tools.sync()

        val transport = StdioServerTransport(
            input = System.`in`.asSource().buffered(),
            output = System.out.asSink().buffered(),
        )
        val session = server.createSession(transport)
        val closed = CompletableDeferred<Unit>()
        session.onClose { closed.complete(Unit) }
        closed.await()
        syncJob.cancel()
        hubJob.cancel()
    }
}
