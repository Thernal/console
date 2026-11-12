package io.thernal.console.mcp.hub

import io.ktor.utils.io.ByteWriteChannel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/** The hub's side of one app connection: sends calls and matches the app's results to them. */
class HubLink(private val output: ByteWriteChannel) {
    private val mutex = Mutex()
    private val nextId = AtomicLong()
    private val pending = ConcurrentHashMap<Long, CompletableDeferred<HubCallResult>>()

    suspend fun call(
        tool: String,
        arguments: JsonObject,
        timeoutMillis: Long,
    ): HubCallResult {
        val id = nextId.incrementAndGet()
        val answer = CompletableDeferred<HubCallResult>()
        pending[id] = answer
        try {
            val message = JsonObject(
                mapOf(
                    "type" to JsonPrimitive("call"),
                    "id" to JsonPrimitive(id),
                    "tool" to JsonPrimitive(tool),
                    "args" to arguments,
                ),
            )
            mutex.withLock { output.writeFrame(message.toString()) }
            return withTimeoutOrNull(timeoutMillis) { answer.await() }
                ?: HubCallResult(isError = true, text = "The app did not answer \"$tool\" within $timeoutMillis ms")
        } finally {
            pending.remove(id)
        }
    }

    fun complete(
        id: Long,
        result: HubCallResult,
    ) {
        pending.remove(id)?.complete(result)
    }

    fun close() {
        pending.values.forEach { it.complete(HubCallResult(isError = true, text = "The app disconnected")) }
        pending.clear()
    }
}
