@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.agent.ui.bridge

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteReadChannel
import io.thernal.console.agent.AgentConfig
import io.thernal.console.agent.AgentConnection
import io.thernal.console.agent.AgentFeed
import io.thernal.console.agent.AgentRegistry
import io.thernal.console.agent.ConsoleAgent
import io.thernal.console.agent.ui.device.AgentDevice
import io.thernal.console.agent.ui.device.readAgentDevice
import io.thernal.console.core.ConsoleInternalApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Shares the [AgentRegistry] with the `console-mcp` hub. The bridge captures nothing itself: addons decide what to
 * share by registering tools and feeds.
 *
 * Feed records are encoded into the [AgentHistory] ring and the live stream as they arrive; a newly connected hub
 * gets the ring first, then the live stream, so a slow or missing hub costs dropped entries, never a slow app. The
 * manifest (tools and feeds) is re-sent whenever the registry or the actions switch changes, and calls from the hub
 * run concurrently through [AgentCallHandler].
 */
internal class AgentBridge(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val selector by lazy { SelectorManager(dispatcher) }
    private val live = MutableSharedFlow<AgentEntry>(
        extraBufferCapacity = LIVE_CAPACITY,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val history = AgentHistory(ConsoleAgent.config.value.historySize)
    private val recordMutex = Mutex()
    private var nextSeq = 0L
    private val collectedFeeds = mutableSetOf<String>()
    private val callHandler = AgentCallHandler(::redactor)
    private var cachedRedactor: Pair<Set<String>, AgentRedactor>? = null

    @OptIn(ExperimentalUuidApi::class)
    private val deviceId = Uuid.random().toString()
    private val platformDevice by lazy { readAgentDevice(deviceId) }

    fun start() {
        scope.launch {
            AgentRegistry.entries.collect { entries ->
                entries.flatMap { it.feeds }
                    .filter { collectedFeeds.add(it.name) }
                    .forEach { feed -> scope.launch { collect(feed) } }
            }
        }
        scope.launch {
            ConsoleAgent.config.collect { history.resize(it.historySize) }
        }
        scope.launch {
            ConsoleAgent.config
                .distinctUntilChanged { old, new -> old.sameLink(new) }
                .collectLatest { config ->
                    if (config.enabled) {
                        stayConnected(config)
                    } else {
                        ConsoleAgent.reportConnection(AgentConnection.Disabled)
                    }
                }
        }
    }

    private suspend fun collect(feed: AgentFeed) {
        feed.records
            .catch { /* A failing feed stops; the others keep streaming. */ }
            .collect { record ->
                val redacted = redactor().redact(record)
                recordMutex.withLock {
                    val seq = nextSeq++
                    val json = AgentMessages.record(feed = feed.name, seq = seq, record = redacted)
                    val entry = AgentEntry(seq = seq, json = json)
                    history.append(entry)
                    live.emit(entry)
                }
            }
    }

    private suspend fun stayConnected(config: AgentConfig) {
        var retryDelay = INITIAL_RETRY_MILLIS
        while (true) {
            ConsoleAgent.reportConnection(AgentConnection.Searching)
            if (connectOnce(config)) {
                retryDelay = INITIAL_RETRY_MILLIS
            }
            delay(retryDelay)
            retryDelay = (retryDelay * 2).coerceAtMost(MAX_RETRY_MILLIS)
        }
    }

    /** Runs one connection until the hub goes away; returns whether the hub was reached at all. */
    private suspend fun connectOnce(config: AgentConfig): Boolean {
        var reached = false
        try {
            aSocket(selector).tcp().connect(hostname = config.hubHost, port = config.hubPort).use { socket ->
                val writer = AgentFrameWriter(socket.openWriteChannel(autoFlush = false))
                val input = socket.openReadChannel()
                writer.write(AgentMessages.hello(device(config)))
                reached = true
                ConsoleAgent.reportConnection(AgentConnection.Connected("${config.hubHost}:${config.hubPort}"))
                coroutineScope {
                    launch { manifests().collect { writer.write(it) } }
                    launch { stream(writer) }
                    serveCalls(input = input, writer = writer)
                    coroutineContext.cancelChildren()
                }
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (ignored: Exception) {
            // Hub not running or the link dropped; the caller retries.
        }
        return reached
    }

    private fun manifests(): Flow<String> {
        val actions = ConsoleAgent.config.map { it.enableActions }.distinctUntilChanged()
        return combine(AgentRegistry.entries, actions) { entries, enableActions ->
            AgentMessages.manifest(entries = entries, enableActions = enableActions)
        }.distinctUntilChanged()
    }

    private suspend fun stream(writer: AgentFrameWriter) {
        live
            .onSubscription { history.snapshot().forEach { emit(it) } }
            .collect { entry -> writer.write(entry.json) }
    }

    /** Reads calls until the hub closes the connection; each call runs in its own coroutine. */
    private suspend fun serveCalls(
        input: ByteReadChannel,
        writer: AgentFrameWriter,
    ) {
        coroutineScope {
            while (true) {
                val message = Json.parseToJsonElement(input.readFrame()) as? JsonObject ?: continue
                if (message["type"]?.jsonPrimitive?.contentOrNull == AgentMessages.TYPE_CALL) {
                    launch { callHandler.handle(message)?.let { writer.write(it) } }
                }
            }
        }
    }

    private fun redactor(): AgentRedactor {
        val keys = AgentRedactor.DEFAULT_KEYS + AgentRegistry.redactedKeys()
        val cached = cachedRedactor
        if (cached != null && cached.first == keys) return cached.second
        return AgentRedactor(keys).also { cachedRedactor = keys to it }
    }

    private fun device(config: AgentConfig): AgentDevice {
        return platformDevice.copy(name = config.deviceName ?: platformDevice.name)
    }

    private fun AgentConfig.sameLink(other: AgentConfig): Boolean {
        return enabled == other.enabled &&
            hubHost == other.hubHost &&
            hubPort == other.hubPort &&
            deviceName == other.deviceName
    }

    private companion object {
        const val LIVE_CAPACITY = 1_024

        // Connecting to a loopback port that nobody listens on fails at once, so retrying often is cheap and lets
        // the app reach a hub that an agent session has just started.
        const val INITIAL_RETRY_MILLIS = 250L
        const val MAX_RETRY_MILLIS = 2_000L
    }
}
